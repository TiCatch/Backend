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

    public static UserResponseDto of(User user) {
        return UserResponseDto.builder()
                .userId(user.getUserId())
                .userNickname(user.getUserNickname())
                .userScore(user.getUserScore())
                .userEmail(user.getEmail())
                .build();
    }
}
