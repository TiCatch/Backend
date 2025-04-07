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
	SERVER_EXCEPTION(500, "서버에서 예측하지 못한 에러가 발생했습니다."),

	// TICKETING
	UNAUTHORIZED_TICKET_ACCESS_EXCEPTION(440, "티켓팅에 접근할 권한이 없습니다."),
	NOT_EXIST_TICKET_EXCEPTION(441, "티켓팅이 존재하지 않습니다."),
	NOT_IN_PROGRESS_TICKET_EXCEPTION(442, "진행 중인 티켓팅이 아닙니다."),
	NOT_EXIST_IN_PROGRESS_TICKET_EXCEPTION(443, "진행 중인 티켓팅이 없습니다."),

	// RESERVATION
	ALREADY_RESERVED_EXCEPTION(450, "이미 선점된 좌석입니다."),

	// JSON
	JSON_PROCESS_EXCEPTION(460, "JSON 데이터 처리 중 오류가 발생했습니다.");

	private final int errorCode;
	private final String errorMessage;
}
