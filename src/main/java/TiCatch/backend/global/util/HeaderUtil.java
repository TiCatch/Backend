package TiCatch.backend.global.util;

import TiCatch.backend.domain.user.dto.TokenDto;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import static TiCatch.backend.global.constant.UserConstants.*;

@Slf4j
@Component
public class HeaderUtil {
    public HttpHeaders setTokenHeaders(TokenDto tokenDto, HttpServletResponse response) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(ACCESS_TOKEN, tokenDto.getAccessToken());
        Cookie refreshTokenCookie = new Cookie(REFRESH_TOKEN, tokenDto.getRefreshToken());
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setMaxAge(REFRESH_TOKEN_MAX_AGE);
        response.addCookie(refreshTokenCookie);

        return headers;
    }
}
