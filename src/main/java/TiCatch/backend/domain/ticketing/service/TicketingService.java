package TiCatch.backend.domain.ticketing.service;

import TiCatch.backend.domain.auth.service.RedisService;
import TiCatch.backend.domain.ticketing.dto.request.CreateTicketingDto;
import TiCatch.backend.domain.ticketing.dto.response.TicketingResponseDto;
import TiCatch.backend.domain.ticketing.dto.response.TicketingWaitingResponseDto;
import TiCatch.backend.domain.ticketing.entity.Ticketing;
import TiCatch.backend.domain.ticketing.entity.TicketingStatus;
import TiCatch.backend.domain.ticketing.repository.TicketingRepository;
import TiCatch.backend.domain.user.entity.User;
import TiCatch.backend.global.exception.NotExistTicketException;
import TiCatch.backend.global.exception.NotInProgressTicketException;
import TiCatch.backend.global.exception.UnAuthorizedTicketAccessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TicketingService {

    private final RedisService redisService;
    private final TicketingRepository ticketingRepository;

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
        validateTicketing(ticketing, responseUserId);   // 티켓팅 유효성 검증 (1. 티켓팅 주인 / 2. 티켓팅 상태)
        redisService.addToWaitingQueue(ticketingId, userId);
        return TicketingWaitingResponseDto.of(ticketingId, responseUserId, redisService.getWaitingQueueRank(ticketingId, userId));
    }

    public TicketingWaitingResponseDto getTicketingWaitingStatus(Long ticketingId, Long userId) {
        return TicketingWaitingResponseDto.of(ticketingId, userId, redisService.getWaitingQueueRank(ticketingId, userId.toString()));
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
