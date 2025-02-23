package TiCatch.backend.domain.ticketing.dto.response;

import TiCatch.backend.domain.history.entity.History;
import TiCatch.backend.domain.ticketing.entity.Ticketing;
import TiCatch.backend.domain.ticketing.entity.TicketingLevel;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class TicketingCompleteResponseDto {
    private Long userId;
    private Long ticketingId;
    private TicketingLevel ticketingLevel;
    private String seatInfo;
    private int ticketingScore;
    private LocalDateTime ticketingStartTime;
    private LocalDateTime ticketingEndTime;

    @Builder
    private TicketingCompleteResponseDto(Long userId, Long ticketingId, TicketingLevel ticketingLevel,
                                         String seatInfo, int ticketingScore, LocalDateTime ticketingStartTime, LocalDateTime ticketingEndTime) {
        this.userId = userId;
        this.ticketingId = ticketingId;
        this.ticketingLevel = ticketingLevel;
        this.seatInfo = seatInfo;
        this.ticketingScore = ticketingScore;
        this.ticketingStartTime = ticketingStartTime;
        this.ticketingEndTime = ticketingEndTime;
    }

    public static TicketingCompleteResponseDto of(Ticketing ticketing, History history) {
        return TicketingCompleteResponseDto.builder()
                .userId(ticketing.getUser().getUserId())
                .ticketingId(ticketing.getTicketingId())
                .ticketingLevel(ticketing.getTicketingLevel())
                .seatInfo(history.getSeatInfo())
                .ticketingScore(history.getTicketingScore())
                .ticketingStartTime(ticketing.getTicketingTime())
                .ticketingEndTime(history.getCreatedDate())
                .build();
    }
}