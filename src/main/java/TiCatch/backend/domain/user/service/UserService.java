package TiCatch.backend.domain.user.service;

import TiCatch.backend.domain.user.dto.response.UserResponseDto;
import TiCatch.backend.domain.user.entity.User;
import jakarta.servlet.http.HttpServletRequest;

public interface UserService {
    //user Id로 유저 정보 조회
    UserResponseDto getUserById(Long userId);

    //토큰에서 유저 정보 조회
    User getUserFromRequest(HttpServletRequest request);
}
