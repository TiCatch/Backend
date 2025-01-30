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
import TiCatch.backend.global.exception.NotExistTicketException;
import TiCatch.backend.global.exception.NotInProgressTicketException;
import TiCatch.backend.global.exception.UnAuthorizedTicketAccessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TicketingService {

    private final RedisService redisService;
    private final TicketingRepository ticketingRepository;
    private final DynamicScheduler dynamicScheduler;

    @Transactional
    public TicketingResponseDto createTicket(CreateTicketingDto createTicketingDto, User user) {
        Ticketing newTicketing = ticketingRepository.save(Ticketing.fromDtoToEntity(createTicketingDto, user, TicketingStatus.WAITING));
        return TicketingResponseDto.of(newTicketing);
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
}
