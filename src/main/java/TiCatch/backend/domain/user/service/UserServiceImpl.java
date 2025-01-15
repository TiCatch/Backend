package TiCatch.backend.domain.user.service;

import TiCatch.backend.domain.auth.util.JwtProvider;
import TiCatch.backend.domain.user.dto.response.UserResponseDto;
import TiCatch.backend.domain.user.entity.Credential;
import TiCatch.backend.domain.user.entity.User;
import TiCatch.backend.domain.user.repository.CredentialRepository;
import TiCatch.backend.domain.user.repository.UserRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import TiCatch.backend.global.exception.NotExistUserException;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final CredentialRepository credentialRepository;

    @Override
    public UserResponseDto getUserById(Long userId) {
        log.info("UserServiceImpl_getUserById -> userId로 유저 정보 조회 시도");
        User user = userRepository.findByUserId(userId).orElseThrow(NotExistUserException::new);

        return user.toUserResponseDto();
    }

    public User getUserFromRequest(HttpServletRequest request) {
        log.info("UserServiceImpl_getUserFromRequest -> 토큰 값으로 유저 정보 조회");
        String accessToken = request.getHeader("Authorization").split(" ")[1];
        jwtProvider.parseClaims(accessToken);
        Claims accessTokenClaims = jwtProvider.parseClaims(accessToken);
        String userEmail = accessTokenClaims.getSubject();

        log.info("email : " + userEmail +" accessToken : " + accessToken);

        // Credential 조회
        Credential credential = credentialRepository.findByEmail(userEmail).orElseThrow(NotExistUserException::new);

        // User 조회
        return userRepository.findByCredential(credential).orElseThrow(NotExistUserException::new);
    }

}
