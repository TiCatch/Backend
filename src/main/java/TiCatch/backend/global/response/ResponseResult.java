package TiCatch.backend.global.response;

import TiCatch.backend.global.exception.ExceptionCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class ResponseResult {

    int statusCode;
    String messages;
    String developerMessage;
    LocalDateTime timestamp;

    public static final ResponseResult successResponse =
            ResponseResult.builder()
                    .statusCode(HttpStatus.OK.value())
                    .messages("성공 :)")
                    .developerMessage("성공하였습니다.")
                    .timestamp(LocalDateTime.now()).build();

    public static final ResponseResult failResponse =
            ResponseResult.builder()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .messages("실패 :(")
                    .developerMessage("실패하였습니다.")
                    .timestamp(LocalDateTime.now()).build();

    public static final ResponseResult exceptionResponse(ExceptionCode exceptionCode, String message) {
        return ResponseResult.builder()
                .statusCode(exceptionCode.getErrorCode())
                .messages(message)
                .developerMessage(exceptionCode.getErrorMessage())
                .timestamp(LocalDateTime.now()).build();
    }
}
