package TiCatch.backend.global.exception;

public class WrongTokenException extends RuntimeException{
    public WrongTokenException() {
        super(ExceptionCode.WRONG_TOKEN_EXCEPTION.getErrorMessage());
    }

    public WrongTokenException(String message) {
        super(message);
    }

}

