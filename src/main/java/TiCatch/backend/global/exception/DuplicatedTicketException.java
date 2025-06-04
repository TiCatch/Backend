package TiCatch.backend.global.exception;

public class DuplicatedTicketException extends RuntimeException {

    public DuplicatedTicketException() {
        super(ExceptionCode.DUPLICATED_TICKET_EXCEPTION.getErrorMessage());
    }

    public DuplicatedTicketException(String message) {
        super(message);
    }
}
