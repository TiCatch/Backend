package TiCatch.backend.domain.auth.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
// 인증되지 않은 사용자가 보호된 리소스에 접근하려고 할 때 처리.( 토큰 없는 경우)
  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response,
                       AuthenticationException authException) throws IOException, ServletException {
    log.error("인증되지 않은 요청 발생! 요청 URI: {}, 에러 메시지: {}", request.getRequestURI(), authException.getMessage());
    response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
  }
}
