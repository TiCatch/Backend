package TiCatch.backend.domain.history.dto.response;

import TiCatch.backend.domain.ticketing.entity.TicketingLevel;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class TicketingHistoryPagingResponse {
    private Long historyId;
    private Long userId;
    private Long ticketingId;
    private String seatInfo;
    private int ticketingScore;
    private TicketingLevel ticketingLevel;
    private LocalDateTime ticketingTime;
}
