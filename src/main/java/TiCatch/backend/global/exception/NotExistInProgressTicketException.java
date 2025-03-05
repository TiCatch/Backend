package TiCatch.backend.global.exception;

public class NotExistInProgressTicketException extends RuntimeException {

    public NotExistInProgressTicketException() {
        super(ExceptionCode.NOT_EXIST_IN_PROGRESS_TICKET_EXCEPTION.getErrorMessage());
    }

    public NotExistInProgressTicketException(String message) {
        super(message);
    }
}
