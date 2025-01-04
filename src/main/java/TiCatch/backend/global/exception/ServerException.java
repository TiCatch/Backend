package TiCatch.backend.global.exception;

public class ServerException extends RuntimeException{
    public ServerException() {
        super(ExceptionCode.SERVER_EXCEPTION.getErrorMessage());
    }
    public ServerException(String message) {
        super(message);
    }
}
