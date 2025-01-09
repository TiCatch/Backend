package TiCatch.backend.domain.history.entity;

import TiCatch.backend.domain.user.entity.User;
import TiCatch.backend.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "history")
public class History extends BaseTimeEntity {

    //예매 기록 ID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long historyId;

    //사용자
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    //예매 좌석정보
    private String historyInfo;

    //예매 난이도
    private int historyLevel;
}
