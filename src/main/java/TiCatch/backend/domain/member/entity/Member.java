package TiCatch.backend.domain.member.entity;

import TiCatch.backend.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "member")
@AllArgsConstructor
@NoArgsConstructor
public class Member extends BaseTimeEntity {

    //멤버 ID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    //멤버 닉네임
    @Column(nullable = false, length = 30)
    private String memberNickname;

    //총 점수
    @Column(nullable = false)
    private String memberScore;

}
