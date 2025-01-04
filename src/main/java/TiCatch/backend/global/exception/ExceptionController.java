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
}
