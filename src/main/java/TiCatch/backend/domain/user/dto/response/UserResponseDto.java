package TiCatch.backend.domain.user.dto.response;

import TiCatch.backend.domain.user.entity.User;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class UserResponseDto {
    private Long userId;
    private String userNickname;
    private int userScore;
    private String userEmail;

    // 🔹 엔티티 -> DTO 변환을 담당하는 static 메서드 추가
    public static UserResponseDto fromEntity(User user) {
        return UserResponseDto.builder()
                .userId(user.getUserId())
                .userNickname(user.getUserNickname())
                .userScore(user.getUserScore())
                .userEmail(user.getEmail())
                .build();
    }
}
