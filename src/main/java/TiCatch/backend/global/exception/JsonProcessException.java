package TiCatch.backend.global.exception;

public class JsonProcessException extends RuntimeException{
    public JsonProcessException() {
        super(ExceptionCode.JSON_PROCESSING_EXCEPTION.getErrorMessage());
    }

    public JsonProcessException(String message) {
        super(message);
    }
}

