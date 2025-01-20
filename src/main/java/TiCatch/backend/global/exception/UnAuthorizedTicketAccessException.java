package TiCatch.backend.global.exception;

public class UnAuthorizedTicketAccessException extends RuntimeException {

    public UnAuthorizedTicketAccessException() {
        super(ExceptionCode.UNAUTHORIZED_TICKET_ACCESS_EXCEPTION.getErrorMessage());
    }

    public UnAuthorizedTicketAccessException(String message) {
        super(message);
    }
}
