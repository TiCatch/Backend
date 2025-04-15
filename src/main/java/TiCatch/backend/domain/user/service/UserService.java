package TiCatch.backend.domain.user.service;

import TiCatch.backend.global.util.JwtProvider;
import TiCatch.backend.domain.user.dto.response.UserResponseDto;
import TiCatch.backend.domain.user.entity.User;
import TiCatch.backend.domain.user.repository.UserRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import TiCatch.backend.global.exception.NotExistUserException;
import static TiCatch.backend.global.constant.UserConstants.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;

    public UserResponseDto getUserById(Long userId) {
        log.info("UserService_getUserById -> userId로 유저 정보 조회 시도");
        User user = userRepository.findByUserId(userId).orElseThrow(NotExistUserException::new);

        return UserResponseDto.of(user);
    }

    public User getUserFromRequest(HttpServletRequest request) {
        log.info("UserService_getUserFromRequest -> 토큰 값으로 유저 정보 조회");

        String authorizationHeader = request.getHeader(HEADER_AUTHORIZATION);
        if (authorizationHeader == null || !authorizationHeader.startsWith(BEARER_PREFIX)) {
            throw new NotExistUserException(); // 예외 처리
        }

        String accessToken = authorizationHeader.substring(7); // "Bearer " 이후의 토큰 부분만 추출
        Claims accessTokenClaims = jwtProvider.parseClaims(accessToken);
        String userEmail = accessTokenClaims.getSubject();

        log.info("email : " + userEmail + " accessToken : " + accessToken);
        return userRepository.findByEmail(userEmail).orElseThrow(NotExistUserException::new);
    }
}
