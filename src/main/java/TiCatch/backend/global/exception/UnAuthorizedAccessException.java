package TiCatch.backend.global.exception;

public class UnAuthorizedAccessException extends RuntimeException{
    public UnAuthorizedAccessException() {
        super(ExceptionCode.UNAUTHORIZED_ACCESS_EXCEPTION.getErrorMessage());
    }

    public UnAuthorizedAccessException(String message) {
        super(message);
    }

}
