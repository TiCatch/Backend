package TiCatch.backend.domain.auth.dto.response;

import TiCatch.backend.domain.auth.dto.TokenDto;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class UserResDto {
    private Long userId;
    private String userNickname;
    private int userScore;
    private String credentialId;
    private String userEmail;
    private boolean visited;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
    private TokenDto tokenDto;
}
