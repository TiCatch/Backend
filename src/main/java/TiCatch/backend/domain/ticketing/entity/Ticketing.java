package TiCatch.backend.domain.ticketing.entity;

import TiCatch.backend.domain.ticketing.dto.request.CreateTicketingDto;
import TiCatch.backend.domain.user.entity.User;
import TiCatch.backend.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity @Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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
    @Enumerated(EnumType.STRING)
    private TicketingLevel ticketingLevel;

    //티켓팅 시간
    @Column(nullable = false)
    private LocalDateTime ticketingTime;

    //티켓팅 상태
    @Enumerated(EnumType.STRING)
    private TicketingStatus ticketingStatus;

    @Builder
    private Ticketing(User user, TicketingLevel ticketingLevel, LocalDateTime ticketingTime, TicketingStatus ticketingStatus) {
        this.user = user;
        this.ticketingLevel = ticketingLevel;
        this.ticketingTime = ticketingTime;
        this.ticketingStatus = ticketingStatus;
    }

    public static Ticketing fromDtoToEntity(CreateTicketingDto createTicketingDto, User user, TicketingStatus ticketingStatus) {
        return Ticketing.builder()
                .user(user)
                .ticketingStatus(ticketingStatus)
                .ticketingLevel(createTicketingDto.getTicketingLevel())
                .ticketingTime(createTicketingDto.getTicketingTime())
                .build();
    }
}
