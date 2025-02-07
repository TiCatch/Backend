package TiCatch.backend.domain.auth.util;

import TiCatch.backend.domain.auth.dto.TokenDto;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class HeaderUtil {
    public HttpHeaders setTokenHeaders(TokenDto tokenDto, HttpServletResponse response) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("access-token", tokenDto.getAccessToken());
        Cookie refreshTokenCookie = new Cookie("refresh-token", tokenDto.getRefreshToken());
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setMaxAge(60 * 60 * 24 * 7);
        response.addCookie(refreshTokenCookie);
        response.setHeader(
                "Set-Cookie",
                String.format(
                        "refresh-token=%s; Path=/; HttpOnly; Secure; SameSite=None; Max-Age=604800",
                        tokenDto.getRefreshToken()
                )
        );
        return headers;
    }
}
