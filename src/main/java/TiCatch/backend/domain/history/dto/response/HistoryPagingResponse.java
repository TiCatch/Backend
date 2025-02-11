package TiCatch.backend.domain.history.dto.response;

import TiCatch.backend.domain.ticketing.entity.TicketingLevel;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class HistoryPagingResponse {
    private Long historyId;
    private Long userId;
    private Long ticketingId;
    private String seatInfo;
    private TicketingLevel ticketingLevel;
    private LocalDateTime ticketingTime;
}
