package TiCatch.backend.domain.auth.dto.response;

import TiCatch.backend.domain.auth.dto.TokenDto;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class LoginResponseDto {
  private TokenDto tokenDto;
  private UserResDto userResDto;
}