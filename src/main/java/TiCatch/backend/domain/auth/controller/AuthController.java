package TiCatch.backend.domain.auth.controller;

import TiCatch.backend.domain.auth.dto.response.LoginResponseDto;
import TiCatch.backend.domain.auth.dto.response.UserResDto;
import TiCatch.backend.domain.auth.service.KakaoAuthService;
import TiCatch.backend.domain.auth.util.HeaderUtil;
import TiCatch.backend.global.response.SingleResponseResult;
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
    private final HeaderUtil headerUtil;

    @GetMapping("/login/kakao")
    public ResponseEntity<SingleResponseResult<UserResDto>> kakaoLogin(@RequestParam String code) {
        log.info("AuthController_kakaoLogin -> 카카오 로그인");
        LoginResponseDto loginResponseDto = kakaoAuthService.kakaoLogin(code);
        return ResponseEntity.ok().headers(headerUtil.setTokenHeaders(loginResponseDto.getTokenDto()))
                .body(new SingleResponseResult<>(loginResponseDto.getUserResDto()));
    }

}