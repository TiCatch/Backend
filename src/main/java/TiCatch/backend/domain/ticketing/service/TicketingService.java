package TiCatch.backend.domain.ticketing.service;

import TiCatch.backend.domain.ticketing.dto.request.CreateTicketingDto;
import TiCatch.backend.domain.ticketing.dto.response.TicketingResponseDto;
import TiCatch.backend.domain.ticketing.entity.Ticketing;
import TiCatch.backend.domain.ticketing.entity.TicketingStatus;
import TiCatch.backend.domain.ticketing.repository.TicketingRepository;
import TiCatch.backend.domain.user.entity.User;
import TiCatch.backend.global.exception.NotExistTicketException;
import TiCatch.backend.global.exception.UnAuthorizedTicketAccessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TicketingService {

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
}
