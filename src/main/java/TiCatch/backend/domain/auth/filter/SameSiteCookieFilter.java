package TiCatch.backend.domain.auth.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import TiCatch.backend.domain.auth.dto.TokenDto;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class SameSiteCookieFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (response instanceof HttpServletResponse httpServletResponse && request instanceof HttpServletRequest httpServletRequest) {

            TokenDto tokenDto = (TokenDto) httpServletRequest.getAttribute("tokenDto");

            if (tokenDto != null) {
                httpServletResponse.setHeader(
                        "Set-Cookie",
                        String.format(
                                "refresh-token=%s; Path=/; HttpOnly; Secure; SameSite=None; Partitioned; Max-Age=604800; Domain=ticatch.vercel.app",
                                tokenDto.getRefreshToken()
                        )
                );
            }
        }
        chain.doFilter(request, response);
    }
}
