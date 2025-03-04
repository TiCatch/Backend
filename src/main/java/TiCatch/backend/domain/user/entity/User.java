package TiCatch.backend.domain.user.entity;


import TiCatch.backend.domain.user.dto.response.UserResponseDto;
import TiCatch.backend.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User extends BaseTimeEntity {

    //사용자 Id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    //사용자 닉네임
    @Column(nullable = false, length = 20)
    private String userNickname;

    //총 점수
    @Column(nullable = false)
    private int userScore;

    @OneToOne
    @JoinColumn(name = "credential_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Credential credential;

    public UserResponseDto toUserResponseDto() {
        return UserResponseDto.builder()
                .userId(userId)
                .userNickname(userNickname)
                .userScore(userScore)
                .credentialId(credential.getCredentialId())
                .userEmail(credential.getEmail())
                .build();
    }

    public void updateUserScore(int score){
        this.userScore += score;
    }
}