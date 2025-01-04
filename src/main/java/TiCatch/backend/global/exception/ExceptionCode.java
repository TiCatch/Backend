package TiCatch.backend.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ExceptionCode {

    SERVER_EXCEPTION(500, "서버에서 예측하지 못한 에러가 발생했습니다.");
    private final int errorCode;
    private final String errorMessage;
}
