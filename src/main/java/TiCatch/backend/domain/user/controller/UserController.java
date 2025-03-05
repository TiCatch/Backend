package TiCatch.backend.domain.user.controller;

import TiCatch.backend.domain.user.dto.response.UserResponseDto;
import TiCatch.backend.domain.user.entity.User;
import TiCatch.backend.global.response.ResponseResult;
import TiCatch.backend.global.response.SingleResponseResult;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import TiCatch.backend.domain.user.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/users")
@RestController
public class UserController {

    private final UserService userService;

    @GetMapping("/{userId}")
    public ResponseResult getUser(@PathVariable Long userId) {
        log.info("UserController_getUser -> userId로 유저 정보 조회");
        UserResponseDto userResponseDto = userService.getUserById(userId);
        return new SingleResponseResult<>(userResponseDto);
    }

    @GetMapping("/")
    public ResponseResult getUserInfo(HttpServletRequest request) {
        log.info("UserController_getUserInfo -> 토큰 값으로 유저 정보 조회");
        User user = userService.getUserFromRequest(request);
        return new SingleResponseResult<>(UserResponseDto.of(user));
    }
}
