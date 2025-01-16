package TiCatch.backend.domain.ticketing.dto.response;

import TiCatch.backend.domain.ticketing.entity.Ticketing;
import TiCatch.backend.domain.ticketing.entity.TicketingLevel;
import TiCatch.backend.domain.ticketing.entity.TicketingStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class TicketingResponseDto {
    private Long ticketingId;
    private Long userId;
    private TicketingLevel ticketingLevel;
    private LocalDateTime ticketingTime;
    private TicketingStatus ticketingStatus;

    @Builder
    private TicketingResponseDto(Long ticketingId, Long userId, TicketingLevel ticketingLevel,
                                 LocalDateTime ticketingTime, TicketingStatus ticketingStatus) {
        this.ticketingId = ticketingId;
        this.userId = userId;
        this.ticketingLevel = ticketingLevel;
        this.ticketingTime = ticketingTime;
        this.ticketingStatus = ticketingStatus;
    }

    public static TicketingResponseDto of(Ticketing ticketing) {
        return TicketingResponseDto.builder()
                .ticketingId(ticketing.getTicketingId())
                .userId(ticketing.getUser().getUserId())
                .ticketingLevel(ticketing.getTicketingLevel())
                .ticketingTime(ticketing.getTicketingTime())
                .ticketingStatus(ticketing.getTicketingStatus())
                .build();
    }
}
