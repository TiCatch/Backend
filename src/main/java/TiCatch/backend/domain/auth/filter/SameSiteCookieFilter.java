package TiCatch.backend.domain.auth.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import jakarta.servlet.Filter;

@Component
public class SameSiteCookieFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (response instanceof HttpServletResponse httpServletResponse) {
            httpServletResponse.setHeader("Set-Cookie",
                    "refresh-token=; Path=/; HttpOnly; Secure; SameSite=None");
        }
        chain.doFilter(request, response);
    }
}