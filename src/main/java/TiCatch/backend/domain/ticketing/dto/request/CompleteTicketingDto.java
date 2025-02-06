package TiCatch.backend.domain.ticketing.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompleteTicketingDto {
    private Long ticketingId;
    private String seatInfo;
}