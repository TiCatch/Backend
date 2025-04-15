package TiCatch.backend.domain.ticketing.service;

import TiCatch.backend.global.exception.*;
import TiCatch.backend.global.service.redis.RedisService;
import TiCatch.backend.domain.history.entity.History;
import TiCatch.backend.domain.history.repository.HistoryRepository;
import TiCatch.backend.domain.ticketing.dto.request.CreateTicketingDto;
import TiCatch.backend.domain.ticketing.dto.request.CompleteTicketingDto;
import TiCatch.backend.domain.ticketing.dto.response.TicketingCompleteResponseDto;
import TiCatch.backend.domain.ticketing.dto.response.TicketingResponseDto;
import TiCatch.backend.domain.ticketing.dto.response.TicketingWaitingResponseDto;
import TiCatch.backend.domain.ticketing.entity.Ticketing;
import TiCatch.backend.domain.ticketing.entity.TicketingStatus;
import TiCatch.backend.domain.ticketing.repository.TicketingRepository;
import TiCatch.backend.domain.user.entity.User;
import TiCatch.backend.global.config.DynamicScheduler;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
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
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.time.LocalDateTime;

import static TiCatch.backend.domain.ticketing.entity.TicketingLevel.*;
import static TiCatch.backend.global.constant.TicketingConstants.*;
import static TiCatch.backend.global.constant.UserConstants.VIRTUAL_USERTYPE;
import static TiCatch.backend.global.constant.UserConstants.VIRTUAL_USER_ID;
import static TiCatch.backend.global.constant.RedisConstants.TICKETING_SEAT_PREFIX;

@Service
@Slf4j
//@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TicketingService {

    private final RedisService redisService;
    private final TicketingRepository ticketingRepository;
    private final DynamicScheduler dynamicScheduler;
    private static Map<String, Map<Integer, Integer>> SECTION_INFORMATION;
    private final RedisTemplate<String, String> redisTemplate;
    private final ReactiveRedisTemplate<String, String> reactiveRedisTemplate;
    private final HistoryRepository historyRepository;

    public TicketingService(RedisService redisService, TicketingRepository ticketingRepository, DynamicScheduler dynamicScheduler, RedisTemplate<String, String> redisTemplate,
                            @Qualifier("reactiveRedisTemplate") ReactiveRedisTemplate<String, String> reactiveRedisTemplate, HistoryRepository historyRepository) {
        this.redisService = redisService;
        this.ticketingRepository = ticketingRepository;
        this.dynamicScheduler = dynamicScheduler;
        this.redisTemplate = redisTemplate;
        this.reactiveRedisTemplate = reactiveRedisTemplate;
        this.historyRepository = historyRepository;
    }

    @PostConstruct
    public void loadSectionInfo() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            InputStream inputStream = new ClassPathResource(SECTION_INFO_FILE_NAME).getInputStream();
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

        addExpiryToControlQueue(newTicketing.getTicketingId(), createTicketingDto.getTicketingTime());

        TicketingResponseDto responseDto = TicketingResponseDto.of(newTicketing);

        String redisKey = TICKETING_SEAT_PREFIX + newTicketing.getTicketingId();

        Flux.fromIterable(SECTION_INFORMATION.entrySet())
                .flatMap(sectionEntry -> {
                    String section = sectionEntry.getKey();
                    Map<Integer, Integer> rowInfo = sectionEntry.getValue();

                    return Flux.fromIterable(rowInfo.entrySet())
                            .flatMap(rowEntry -> {
                                int row = rowEntry.getKey();
                                int cols = rowEntry.getValue();

                                return Flux.range(1, cols).map(seat -> {
                                    String seatKey = section + ROW + row + COL + seat;
                                    return Map.entry(seatKey, "0");
                                });
                            });
                })
                .flatMap(entry -> reactiveRedisTemplate.opsForHash().put(redisKey, entry.getKey(), entry.getValue()))
                .subscribe();

        return Mono.just(responseDto);
    }

    public TicketingResponseDto getInProgressOrWaitingTicket(User user) {
        Ticketing ticketing = ticketingRepository.findByUserAndTicketingStatusIn(user, List.of(TicketingStatus.IN_PROGRESS, TicketingStatus.WAITING)).orElseThrow(NotExistInProgressTicketException::new);
        return TicketingResponseDto.of(ticketing);
    }

    private void addExpiryToControlQueue(Long ticketingId, LocalDateTime ticketingTime) {
        long nowMillis = Instant.now().toEpochMilli();
        long startTime = ticketingTime.toEpochSecond(ZoneOffset.of("+09:00")) * 1000;
        long endTime = ticketingTime.plusMinutes(30).toEpochSecond(ZoneOffset.of("+09:00")) * 1000;

        long startExpireTime = (startTime - nowMillis) / 1000;
        long endExpireTime = (endTime - nowMillis) / 1000;
        redisService.addExpiryToControlQueue(ticketingId, startExpireTime, TicketingStatus.IN_PROGRESS);
        redisService.addExpiryToControlQueue(ticketingId, endExpireTime, TicketingStatus.COMPLETED);
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
        dynamicScheduler.startScheduler(ticketingId);
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
        if(userId.startsWith(VIRTUAL_USERTYPE)) {
            return VIRTUAL_USER_ID;
        }
        return Long.valueOf(userId);
    }

    private void validateTicketing(Ticketing ticketing, Long userId) {
        if(!ticketing.getUser().getUserId().equals(userId) && !userId.equals(VIRTUAL_USER_ID)) {
            throw new UnAuthorizedTicketAccessException();
        }
        if(ticketing.getTicketingStatus() != TicketingStatus.IN_PROGRESS) {
            throw new NotInProgressTicketException();
        }
    }

    public Mono<Map<String, Boolean>> getTicketingSeats(Long ticketingId) {
        String redisKey = TICKETING_SEAT_PREFIX + ticketingId;
        return reactiveRedisTemplate.opsForHash().entries(redisKey)
                .collectMap(
                        entry -> entry.getKey().toString(),
                        entry -> "1".equals(entry.getValue()) // 1이면 true, 0이면 false로 변환
                );
    }

    public Mono<Map<String, Boolean>> getSectionSeats(Long ticketingId, String section) {
        String redisKey = TICKETING_SEAT_PREFIX + ticketingId;
        return reactiveRedisTemplate.opsForHash().entries(redisKey)
                .filter(entry -> entry.getKey().toString().startsWith(section + ":"))
                .collectMap(
                        entry -> entry.getKey().toString(),
                        entry -> "1".equals(entry.getValue()) // 1이면 true, 0이면 false로 변환
                );
    }

    public void isAvailable(Long ticketingId, String seatKey) {
        String redisKey = TICKETING_SEAT_PREFIX + ticketingId;
        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();

        String seatStatus = hashOperations.get(redisKey, seatKey);
        if (seatStatus.equals("1")) {
            throw new AlreadyReservedException();
        }
    }

    @Transactional
    public TicketingResponseDto cancelTicket(Long ticketingId, User user) {
        Ticketing ticketing = ticketingRepository.findById(ticketingId).orElseThrow(NotExistTicketException::new);
        if(!ticketing.getUser().getUserId().equals(user.getUserId())) {
            throw new UnAuthorizedTicketAccessException();
        }
        log.info("ticketingId : {} 티켓팅을 취소했습니다. ",ticketingId);
        ticketing.changeTicketingStatus(TicketingStatus.CANCELED);
        dynamicScheduler.stopScheduler(ticketing.getTicketingId());
        redisTemplate.delete(TICKETING_SEAT_PREFIX + ticketing.getTicketingId());
        return TicketingResponseDto.of(ticketing);
    }

    @Transactional
    public TicketingCompleteResponseDto ticketingComplete(CompleteTicketingDto completeTicketingDto, User user) {
        Ticketing ticketing = ticketingRepository.findById(completeTicketingDto.getTicketingId())
                .orElseThrow(NotExistTicketException::new);

        Object seatScore = redisTemplate.opsForHash().get(SEAT_SCORE_FILE_NAME, completeTicketingDto.getSeatInfo());
        double levelScore = LEVEL_SCORE_DEFAULT;
        if(ticketing.getTicketingLevel() == NORMAL){
            levelScore = LEVEL_SCORE_NORMAL;
        }else if(ticketing.getTicketingLevel() == HARD){
            levelScore = LEVEL_SCORE_HARD;
        }
        int ticketingScore = (int)(levelScore * Integer.parseInt(seatScore.toString()));

        ticketing.changeTicketingStatus(TicketingStatus.COMPLETED);
        History history = historyRepository.save(History.of(completeTicketingDto, user, ticketing,ticketingScore));
        user.updateUserScore(ticketingScore);
        log.info("ticketingId : {} 티켓팅이 종료했습니다. ",ticketing.getTicketingId());
        dynamicScheduler.stopScheduler(ticketing.getTicketingId());
        redisTemplate.delete(TICKETING_SEAT_PREFIX + ticketing.getTicketingId());
        return TicketingCompleteResponseDto.of(ticketing, history);
    }
}