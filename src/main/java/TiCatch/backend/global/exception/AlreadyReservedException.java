package TiCatch.backend.global.exception;

public class AlreadyReservedException extends RuntimeException {
    public AlreadyReservedException() {
        super(ExceptionCode.ALREADY_RESERVED_EXCEPTION.getErrorMessage());
    }

    public AlreadyReservedException(String message) {
        super(message);
    }

}
