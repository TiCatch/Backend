package TiCatch.backend.domain.ticketing.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class TicketingWaitingResponseDto {
    private Long ticketingId;
    private Long userId;
    private Long waitingNumber;

    @Builder
    private TicketingWaitingResponseDto(Long ticketingId, Long userId, Long waitingNumber) {
        this.ticketingId = ticketingId;
        this.userId = userId;
        this.waitingNumber = waitingNumber;
    }

    public static TicketingWaitingResponseDto of(Long ticketingId, Long userId, Long waitingNumber) {
        return TicketingWaitingResponseDto.builder()
                .ticketingId(ticketingId)
                .userId(userId)
                .waitingNumber(waitingNumber)
                .build();
    }
}
