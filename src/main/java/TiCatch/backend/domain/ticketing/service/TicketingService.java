package TiCatch.backend.domain.ticketing.service;

import TiCatch.backend.domain.ticketing.dto.request.CreateTicketingDto;
import TiCatch.backend.domain.ticketing.dto.response.TicketingResponseDto;
import TiCatch.backend.domain.ticketing.entity.Ticketing;
import TiCatch.backend.domain.ticketing.entity.TicketingStatus;
import TiCatch.backend.domain.ticketing.repository.TicketingRepository;
import TiCatch.backend.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
