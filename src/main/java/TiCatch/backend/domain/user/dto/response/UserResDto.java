package TiCatch.backend.domain.user.dto.response;

import TiCatch.backend.domain.user.dto.TokenDto;
import TiCatch.backend.domain.user.entity.User;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class UserResDto {
    private Long userId;
    private String userNickname;
    private int userScore;
    private String userEmail;
    private boolean visited;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
    private TokenDto tokenDto;

    @Builder
    private UserResDto(Long userId, String userNickname, int userScore, String userEmail,
                       LocalDateTime createdDate, LocalDateTime modifiedDate, TokenDto tokenDto) {
        this.userId = userId;
        this.userNickname = userNickname;
        this.userScore = userScore;
        this.userEmail = userEmail;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
        this.tokenDto = tokenDto;
    }

    public static UserResDto of(User user, TokenDto tokenDto) {
        return UserResDto.builder()
                .userId(user.getUserId())
                .userNickname(user.getUserNickname())
                .userScore(user.getUserScore())
                .userEmail(user.getEmail())
                .createdDate(user.getCreatedDate())
                .modifiedDate(user.getModifiedDate())
                .tokenDto(tokenDto)
                .build();
    }
}
