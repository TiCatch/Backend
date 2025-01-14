package TiCatch.backend.domain.user.dto.response;

import lombok.*;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class UserResponseDto {
    private Long userId;
    private String userNickname;
    private int userScore;
    private String credentialId;
    private String userEmail;
}