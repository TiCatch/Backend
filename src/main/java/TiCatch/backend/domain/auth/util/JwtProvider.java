package TiCatch.backend.domain.auth.util;

import TiCatch.backend.domain.auth.dto.TokenDto;
import TiCatch.backend.domain.auth.dto.UserDto;
import TiCatch.backend.global.exception.UnAuthorizedAccessException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import TiCatch.backend.global.exception.WrongTokenException;
import TiCatch.backend.global.exception.ExpiredTokenException;
import static TiCatch.backend.global.constant.UserConstants.*;

import java.security.Key;
import java.util.Date;

@Slf4j
@Component
public class JwtProvider {

    private final Key key;
    @Value("${jwt.bearer.type}")
    private String BEARER_TYPE;
    @Value("${jwt.bearer.prefix}")
    private String BEARER_PREFIX;

    public JwtProvider(@Value("${jwt.secret}") String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public TokenDto generateTokenDto(String email) {
        long now = (new Date()).getTime();

        Date accessTokenExpiresIn = new Date(now + ACCESS_TOKEN_EXPIRE_TIME);
        String accessToken = Jwts.builder()
                .setSubject(email)
                .setExpiration(accessTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();

        String refreshToken = Jwts.builder()
                .setExpiration(new Date(now + REFRESH_TOKEN_EXPIRE_TIME))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();

        return TokenDto.builder()
                .grantType(BEARER_TYPE)
                .accessToken(BEARER_PREFIX + accessToken)
                .accessTokenExpiresIn(accessTokenExpiresIn.getTime())
                .refreshToken(refreshToken)
                .build();
    }

    public Authentication getAuthentication(String accessToken) {
        Claims claims = parseClaims(accessToken);
        String email = claims.getSubject();
        if (email == null || email.isEmpty()) {
            throw new UnAuthorizedAccessException();
        }
        UserDto userDto = UserDto.builder()
                .email(email)
                .build();
        return new UsernamePasswordAuthenticationToken(userDto, accessToken, null);
    }

    public boolean validateToken(String token, boolean isReissueRequest) {

        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            if (isReissueRequest) {
                log.info("만료된 토큰이지만, 재발급 요청이므로 통과");
                return false;
            }
            throw new ExpiredTokenException();
        } catch (SecurityException | MalformedJwtException | UnsupportedJwtException e) {
            throw new WrongTokenException();
        }
    }

    public Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(accessToken)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }
}
