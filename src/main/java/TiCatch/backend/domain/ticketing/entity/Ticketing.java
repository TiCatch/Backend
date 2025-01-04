package TiCatch.backend.domain.ticketing.entity;

import TiCatch.backend.domain.member.entity.Member;
import TiCatch.backend.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@Table(name = "ticketing")
@AllArgsConstructor
@NoArgsConstructor
public class Ticketing extends BaseTimeEntity {

    //티켓팅 ID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ticketingId;

    //멤버
    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    //티켓팅 레벨
    @Column(nullable = false, length = 20)
    private int ticketingLevel;

    //티켓팅 시간
    @Column(nullable = false)
    private LocalDate ticketingTime;

    //티켓팅 상태
    @Column(nullable = false, length = 20)
    private String ticketingState;
}
