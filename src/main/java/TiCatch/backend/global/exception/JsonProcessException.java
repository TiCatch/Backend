package TiCatch.backend.global.exception;

public class JsonProcessException extends RuntimeException{
    public JsonProcessException() {
        super(ExceptionCode.JSON_PROCESS_EXCEPTION.getErrorMessage());
    }

    public JsonProcessException(String message) {
        super(message);
    }
}

