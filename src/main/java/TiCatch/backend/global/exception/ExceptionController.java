package TiCatch.backend.global.exception;

import TiCatch.backend.global.response.ResponseResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice(basePackages = "TiCatch.backend")
public class ExceptionController {
    @ExceptionHandler(ServerException.class)
    public ResponseResult ServerException(ServerException err) {
        log.info("Error : {}", err.getClass());
        log.info("Error Message : {}", err.getMessage());
        return ResponseResult.exceptionResponse(ExceptionCode.SERVER_EXCEPTION, err.getMessage());
    }

    @ExceptionHandler(NotExistUserException.class)
    public ResponseResult NotExistUserException(NotExistUserException err) {
        log.info("Error : {}", err.getClass());
        log.info("Error Message : {}", err.getMessage());
        return ResponseResult.exceptionResponse(ExceptionCode.INVALID_REFRESH_TOKEN_EXCEPTION, err.getMessage());
    }

    @ExceptionHandler(ExpiredTokenException.class)
    public ResponseResult ExpiredTokenException(ExpiredTokenException err) {
        log.info("Error : {}", err.getClass());
        log.info("Error Message : {}", err.getMessage());
        return ResponseResult.exceptionResponse(ExceptionCode.EXPIRED_TOKEN_EXCEPTION, err.getMessage());
    }

    @ExceptionHandler(UnAuthorizedAccessException.class)
    public ResponseResult UnAuthorizedAccessException(UnAuthorizedAccessException err) {
        log.info("Error : {}", err.getClass());
        log.info("Error Message : {}", err.getMessage());
        return ResponseResult.exceptionResponse(ExceptionCode.UNAUTHORIZED_ACCESS_EXCEPTION, err.getMessage());
    }

    @ExceptionHandler(WrongTokenException.class)
    public ResponseResult WrongTokenException(WrongTokenException err) {
        log.info("Error : {}", err.getClass());
        log.info("Error Message : {}", err.getMessage());
        return ResponseResult.exceptionResponse(ExceptionCode.WRONG_TOKEN_EXCEPTION, err.getMessage());
    }

    @ExceptionHandler(InvalidRefreshTokenException.class)
    public ResponseResult InvalidRefreshTokenException(InvalidRefreshTokenException err) {
        log.info("Error : {}", err.getClass());
        log.info("Error Message : {}", err.getMessage());
        return ResponseResult.exceptionResponse(ExceptionCode.INVALID_REFRESH_TOKEN_EXCEPTION, err.getMessage());
    }

    @ExceptionHandler(UnAuthorizedTicketAccessException.class)
    public ResponseResult UnAuthorizedTicketAccessException(UnAuthorizedTicketAccessException err) {
        log.info("Error : {}", err.getClass());
        log.info("Error Message : {}", err.getMessage());
        return ResponseResult.exceptionResponse(ExceptionCode.UNAUTHORIZED_TICKET_ACCESS_EXCEPTION, err.getMessage());
    }

    @ExceptionHandler(NotExistTicketException.class)
    public ResponseResult NotExistTicketException(NotExistTicketException err) {
        log.info("Error : {}", err.getClass());
        log.info("Error Message : {}", err.getMessage());
        return ResponseResult.exceptionResponse(ExceptionCode.NOT_EXIST_TICKET_EXCEPTION, err.getMessage());
    }
}
