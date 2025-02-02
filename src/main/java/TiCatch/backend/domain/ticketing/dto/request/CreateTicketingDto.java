package TiCatch.backend.domain.ticketing.dto.request;

import TiCatch.backend.domain.ticketing.entity.TicketingLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateTicketingDto {
    private TicketingLevel ticketingLevel;
    private LocalDateTime ticketingTime;
}