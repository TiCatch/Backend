package TiCatch.backend.domain.user.controller;

import TiCatch.backend.domain.user.dto.TokenDto;
import TiCatch.backend.domain.user.dto.response.LoginResponseDto;
import TiCatch.backend.domain.user.dto.response.UserResDto;
import TiCatch.backend.domain.user.service.KakaoAuthService;
import TiCatch.backend.global.response.SingleResponseResult;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final KakaoAuthService kakaoAuthService;

    @GetMapping("/login/kakao")
    public ResponseEntity<SingleResponseResult<UserResDto>> kakaoLogin(@RequestParam("code") String code, HttpServletResponse response) {
        LoginResponseDto loginResponseDto = kakaoAuthService.kakaoLogin(code, response);
        return ResponseEntity.ok(new SingleResponseResult<>(loginResponseDto.getUserResDto()));
    }

    @GetMapping("/reissue")
    public ResponseEntity<SingleResponseResult<TokenDto>> refreshAccessToken(@CookieValue(value = "refresh-token", required = false) String refreshToken, HttpServletResponse response) {
        TokenDto newTokenDto = kakaoAuthService.reissueAccessToken(refreshToken, response);
        return ResponseEntity.ok(new SingleResponseResult<>(newTokenDto));
    }

    @PostMapping("/logout")
    public ResponseEntity<SingleResponseResult<String>> logout(@CookieValue(value = "refresh-token", required = false) String refreshToken, HttpServletResponse response) {
        kakaoAuthService.logout(refreshToken, response);
        return ResponseEntity.ok(new SingleResponseResult<>("로그아웃 성공!"));
    }
}