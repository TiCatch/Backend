package TiCatch.backend.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ExceptionCode {
	INVALID_REFRESH_TOKEN_EXCEPTION(430,"유효하지 않은 토큰입니다."),
	EXPIRED_TOKEN_EXCEPTION(431,"만료된 토큰입니다."),
	UNAUTHORIZED_ACCESS_EXCEPTION(432,"접근 권한이 없습니다."),
	WRONG_TOKEN_EXCEPTION(433,"잘못된 토큰입니다."),
	NOT_EXIST_USER_EXCEPTION(450,"사용자가 존재하지 않습니다."),
	SERVER_EXCEPTION(500, "서버에서 예측하지 못한 에러가 발생했습니다.");

	private final int errorCode;
	private final String errorMessage;
}
