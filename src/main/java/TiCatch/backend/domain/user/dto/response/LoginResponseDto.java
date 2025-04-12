package TiCatch.backend.domain.user.dto.response;

import TiCatch.backend.domain.user.dto.TokenDto;
import lombok.Builder;
import lombok.Getter;

@Getter
public class LoginResponseDto {
  private TokenDto tokenDto;
  private UserResDto userResDto;

  @Builder
  private LoginResponseDto(TokenDto tokenDto, UserResDto userResDto) {
    this.tokenDto = tokenDto;
    this.userResDto = userResDto;
  }

  public static LoginResponseDto of(UserResDto userResDto, TokenDto tokenDto) {
    return LoginResponseDto.builder()
            .tokenDto(tokenDto)
            .userResDto(userResDto)
            .build();
  }
}
