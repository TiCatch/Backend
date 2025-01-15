package TiCatch.backend.domain.ticketing.entity;

import TiCatch.backend.domain.user.entity.User;
import TiCatch.backend.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "ticketing")
public class Ticketing extends BaseTimeEntity {

    //티켓팅 ID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ticketingId;

    //사용자
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    //티켓팅 레벨
    @Enumerated
    private TicketingLevel ticketingLevel;

    //티켓팅 시간
    @Column(nullable = false)
    private LocalDateTime ticketingTime;

    //티켓팅 상태
    @Enumerated
    private TicketingStatus ticketingStatus;
}
