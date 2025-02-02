package TiCatch.backend.global.exception;

public class NotInProgressTicketException extends RuntimeException {

    public NotInProgressTicketException() {
        super(ExceptionCode.NOT_IN_PROGRESS_TICKET_EXCEPTION.getErrorMessage());
    }

    public NotInProgressTicketException(String message) {
        super(message);
    }
}
