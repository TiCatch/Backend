package TiCatch.backend.domain.auth.filter;

import TiCatch.backend.domain.auth.util.JwtProvider;
import TiCatch.backend.global.exception.ExpiredTokenException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import static TiCatch.backend.global.constant.UserConstants.*;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtProvider jwtProvider;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                  FilterChain filterChain) throws ServletException, IOException {

    String requestURI = request.getRequestURI();
    String jwt = resolveToken(request);

    boolean isReissueRequest = requestURI.equals(REISSUE_REQUEST);

    if (!StringUtils.hasText(jwt) && !isReissueRequest) {
      filterChain.doFilter(request, response);
      return;
    }

    boolean isValidToken = jwtProvider.validateToken(jwt, isReissueRequest);

    if (isValidToken) {
      Authentication authentication = jwtProvider.getAuthentication(jwt);
      log.info("인증된 사용자: {}", authentication != null ? authentication.getName() : "인증 실패");
      SecurityContextHolder.getContext().setAuthentication(authentication);
      log.info("SecurityContextHolder 저장된 인증 정보: {}", SecurityContextHolder.getContext().getAuthentication());
    } else if (isReissueRequest) {
      log.info("만료된 토큰이지만, 재발급 요청이므로 필터 통과시키기");
    } else {
      log.error("만료된 토큰입니다.");
      throw new ExpiredTokenException();
    }

    filterChain.doFilter(request, response);
  }

  private String resolveToken(HttpServletRequest request) {
    String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
    log.info("Authorization Header: {}", bearerToken);

    if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
      return bearerToken.substring(7);
    }

    return null;
  }

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    String requestURI = request.getRequestURI();

    return requestURI.startsWith(SWAGGER) ||
            requestURI.startsWith(API_DOCS) ||
            requestURI.startsWith(WEBJARS) ||
            requestURI.startsWith(STATIC) ||
            requestURI.equals(FAVICON) ||
            requestURI.startsWith(ERROR);
  }
}
