package TiCatch.backend.domain.auth.controller;

import TiCatch.backend.domain.auth.dto.TokenDto;
import TiCatch.backend.domain.auth.dto.response.LoginResponseDto;
import TiCatch.backend.domain.auth.dto.response.UserResDto;
import TiCatch.backend.domain.auth.service.KakaoAuthService;
import TiCatch.backend.domain.auth.service.RedisService;
import TiCatch.backend.domain.auth.util.HeaderUtil;
import TiCatch.backend.global.response.SingleResponseResult;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
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
    private final RedisService redisService;

    @GetMapping("/login/kakao")
    public ResponseEntity<SingleResponseResult<UserResDto>> kakaoLogin(@RequestParam("code") String code, HttpServletRequest request, HttpServletResponse response) {
        log.info("카카오 로그인 요청 받은 인증 코드: {}", code);
        LoginResponseDto loginResponseDto = kakaoAuthService.kakaoLogin(code);
        request.setAttribute("tokenDto", loginResponseDto.getTokenDto());
        HttpHeaders headers = headerUtil.setTokenHeaders(loginResponseDto.getTokenDto(), response);
        log.info("카카오 로그인 성공! 유저 정보: {}", loginResponseDto.getUserResDto());
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

    @PostMapping("/logout")
    public ResponseEntity<SingleResponseResult<String>> logout(@CookieValue(value = "refresh-token", required = false) String refreshToken, HttpServletResponse response) {
        Cookie refreshTokenCookie = new Cookie("refresh-token", null);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(0);
        response.addCookie(refreshTokenCookie);
        if (refreshToken != null) {
            redisService.deleteValues(refreshToken);
        }
        return ResponseEntity.ok(new SingleResponseResult<>("로그아웃 성공!"));
    }
}