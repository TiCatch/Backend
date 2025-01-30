package TiCatch.backend.domain.ticketing.service;

import TiCatch.backend.domain.auth.service.RedisService;
import TiCatch.backend.domain.ticketing.dto.request.CreateTicketingDto;
import TiCatch.backend.domain.ticketing.dto.response.TicketingResponseDto;
import TiCatch.backend.domain.ticketing.dto.response.TicketingWaitingResponseDto;
import TiCatch.backend.domain.ticketing.entity.Ticketing;
import TiCatch.backend.domain.ticketing.entity.TicketingStatus;
import TiCatch.backend.domain.ticketing.repository.TicketingRepository;
import TiCatch.backend.domain.user.entity.User;
import TiCatch.backend.global.config.DynamicScheduler;
import TiCatch.backend.global.exception.AlreadyReservedException;
import TiCatch.backend.global.exception.NotExistTicketException;
import TiCatch.backend.global.exception.NotInProgressTicketException;
import TiCatch.backend.global.exception.UnAuthorizedTicketAccessException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;


@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TicketingService {

    private final RedisService redisService;
    private final TicketingRepository ticketingRepository;
    private final DynamicScheduler dynamicScheduler;
    private static Map<String, Map<Integer, Integer>> SECTION_INFORMATION;
    private final RedisTemplate<String, String> redisTemplate;
    private final ReactiveRedisTemplate<String, String> reactiveRedisTemplate;

    @PostConstruct
    public void loadSectionInfo() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            InputStream inputStream = new ClassPathResource("section_info.json").getInputStream();
            SECTION_INFORMATION = objectMapper.readValue(inputStream, new TypeReference<Map<String, Map<Integer, Integer>>>() {});
        } catch (IOException e) {
            throw new RuntimeException("좌석을 불러오는 데 실패했습니다.", e);
        }
    }

    @Transactional
    public Mono<TicketingResponseDto> createTicket(CreateTicketingDto createTicketingDto, User user) {
        Ticketing newTicketing = ticketingRepository.save(
                Ticketing.fromDtoToEntity(createTicketingDto, user, TicketingStatus.WAITING)
        );

        String redisKey = "ticketingId:" + user.getUserId();

        return Flux.fromIterable(SECTION_INFORMATION.entrySet())
                .flatMap(sectionEntry -> {
                    String section = sectionEntry.getKey();
                    Map<Integer, Integer> rowInfo = sectionEntry.getValue();

                    return Flux.fromIterable(rowInfo.entrySet())
                            .flatMap(rowEntry -> {
                                int row = rowEntry.getKey();
                                int cols = rowEntry.getValue();

                                return Flux.range(1, cols).map(seat -> {
                                            String seatKey = section + ":R" + row + ":C" + seat;
                                            return Map.entry(seatKey, "0");
                                        });
                            });
                })
                .flatMap(entry -> reactiveRedisTemplate.opsForHash().put(redisKey, entry.getKey(), entry.getValue()))
                .then(Mono.just(TicketingResponseDto.of(newTicketing)));
    }

    public TicketingResponseDto getTicket(Long ticketingId, User user) {
        Ticketing ticketing = ticketingRepository.findById(ticketingId).orElseThrow(NotExistTicketException::new);
        if(!ticketing.getUser().getUserId().equals(user.getUserId())) {
            throw new UnAuthorizedTicketAccessException();
        }
        return TicketingResponseDto.of(ticketing);
    }

    @Transactional
    public TicketingWaitingResponseDto addTicketingWaitingQueue(Long ticketingId, String userId) {
        Long responseUserId = assignUserIdWithVirtualUser(userId);
        Ticketing ticketing = ticketingRepository.findById(ticketingId).orElseThrow(NotExistTicketException::new);
        validateTicketing(ticketing, responseUserId);
        redisService.addToWaitingQueue(ticketingId, userId);
        dynamicScheduler.startScheduler(ticketingId, 3);
        return TicketingWaitingResponseDto.of(ticketingId, responseUserId, redisService.getWaitingQueueRank(ticketingId, userId));
    }

    public TicketingWaitingResponseDto getTicketingWaitingStatus(Long ticketingId, Long userId) {
        Long rank = redisService.getWaitingQueueRank(ticketingId, userId.toString());
        if(rank == -1L) {
            dynamicScheduler.stopScheduler(ticketingId);
            redisService.deleteWaitingQueue(ticketingId);
        }
        return TicketingWaitingResponseDto.of(ticketingId, userId, rank);
    }

    private Long assignUserIdWithVirtualUser(String userId) {
        if(userId.startsWith("VIRTUAL:")) {
            return 0L;
        }
        return Long.valueOf(userId);
    }

    private void validateTicketing(Ticketing ticketing, Long userId) {
        if(!ticketing.getUser().getUserId().equals(userId) && !userId.equals(0L)) {
            throw new UnAuthorizedTicketAccessException();
        }
        if(ticketing.getTicketingStatus() != TicketingStatus.IN_PROGRESS) {
            throw new NotInProgressTicketException();
        }
    }

    public Mono<Map<String, Boolean>> getUserSeats(User user) {
        String redisKey = "ticketingId:" + user.getUserId();
        return reactiveRedisTemplate.opsForHash().entries(redisKey)
                .collectMap(
                        entry -> entry.getKey().toString(),
                        entry -> "1".equals(entry.getValue()) // 1이면 true, 0이면 false로 변환
                );
    }

    public Mono<Map<String, Boolean>> getSectionSeats(User user, String section) {
        String redisKey = "ticketingId:" + user.getUserId();
        return reactiveRedisTemplate.opsForHash().entries(redisKey)
                .filter(entry -> entry.getKey().toString().startsWith(section + ":"))
                .collectMap(
                        entry -> entry.getKey().toString(),
                        entry -> "1".equals(entry.getValue()) // 1이면 true, 0이면 false로 변환
                );
    }

    // 선택한 좌석이 예매 가능한지 확인
    public void isAvailable(User user, String seatKey) {
        String redisKey = "ticketingId:" + user.getUserId();
        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();

        String seatStatus = hashOperations.get(redisKey, seatKey);
        if ("1".equals(seatStatus)) {
            throw new AlreadyReservedException();
        }
    }
}
