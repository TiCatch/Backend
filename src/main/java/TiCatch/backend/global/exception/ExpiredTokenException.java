package TiCatch.backend.global.exception;

public class ExpiredTokenException extends RuntimeException {
    public ExpiredTokenException() {
        super(ExceptionCode.EXPIRED_TOKEN_EXCEPTION.getErrorMessage());
    }

    public ExpiredTokenException(String message) {
        super(message);
    }

}
