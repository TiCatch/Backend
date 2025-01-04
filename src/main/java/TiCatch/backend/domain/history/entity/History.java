package TiCatch.backend.domain.history.entity;

import TiCatch.backend.domain.member.entity.Member;
import TiCatch.backend.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "history")
@AllArgsConstructor
@NoArgsConstructor
public class History extends BaseTimeEntity {

    //예매 기록 ID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long historyId;

    //멤버
    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    //예매 좌석정보
    private String historyInfo;

    //예매 난이도
    private int historyLevel;
}
