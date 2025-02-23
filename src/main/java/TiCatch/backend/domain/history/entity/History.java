package TiCatch.backend.domain.history.entity;

import TiCatch.backend.domain.ticketing.dto.request.CompleteTicketingDto;
import TiCatch.backend.domain.ticketing.entity.Ticketing;
import TiCatch.backend.domain.ticketing.entity.TicketingLevel;
import TiCatch.backend.domain.user.entity.User;
import TiCatch.backend.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "history")
public class History extends BaseTimeEntity {

    // 예매 기록 ID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long historyId;

    // 사용자
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 티켓팅 ID
    private Long ticketingId;

    // 예매 좌석정보
    private String seatInfo;

    // 티켓팅 난이도
    @Enumerated(EnumType.STRING)
    private TicketingLevel ticketingLevel;

    // 티켓팅 점수
    private int ticketingScore;

    // 티켓팅 시간
    private LocalDateTime ticketingTime;

    public static History of(CompleteTicketingDto completeTicketingDto, User user, Ticketing ticketing, int ticketingScore) {
        return History.builder()
                .user(user)
                .ticketingId(ticketing.getTicketingId())
                .seatInfo(completeTicketingDto.getSeatInfo())
                .ticketingLevel(ticketing.getTicketingLevel())
                .ticketingTime(ticketing.getTicketingTime())
                .ticketingScore(ticketingScore)
                .build();
    }

}