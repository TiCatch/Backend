package TiCatch.backend.domain.ticketing.dto.request;

import TiCatch.backend.domain.ticketing.entity.TicketingLevel;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CreateTicketingDto {
    private TicketingLevel ticketingLevel;
    private LocalDateTime ticketingTime;
}