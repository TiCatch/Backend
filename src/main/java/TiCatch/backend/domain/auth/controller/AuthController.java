package TiCatch.backend.domain.auth.controller;

import TiCatch.backend.domain.auth.dto.TokenDto;
import TiCatch.backend.domain.auth.dto.response.LoginResponseDto;
import TiCatch.backend.domain.auth.dto.response.UserResDto;
import TiCatch.backend.domain.auth.service.KakaoAuthService;
import TiCatch.backend.domain.auth.util.HeaderUtil;
import TiCatch.backend.global.response.SingleResponseResult;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final KakaoAuthService kakaoAuthService;
    private final HeaderUtil headerUtil;

    @GetMapping("/login/kakao")
    public ResponseEntity<SingleResponseResult<UserResDto>> kakaoLogin(@RequestParam("code") String code, HttpServletResponse response) {
        LoginResponseDto loginResponseDto = kakaoAuthService.kakaoLogin(code);
        HttpHeaders headers = headerUtil.setTokenHeaders(loginResponseDto.getTokenDto(), response);
        return ResponseEntity.ok().headers(headers)
                .body(new SingleResponseResult<>(loginResponseDto.getUserResDto()));
    }

    @GetMapping("/reissue")
    public ResponseEntity<SingleResponseResult<TokenDto>> refreshAccessToken(@CookieValue(value = "refresh-token", required = false) String refreshToken , HttpServletResponse response) {
        if (refreshToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        try {
            TokenDto newTokenDto = kakaoAuthService.reissueAccessToken(refreshToken);
            response.setHeader("access-token", newTokenDto.getAccessToken());
            return ResponseEntity.ok(new SingleResponseResult<>(newTokenDto));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }
}