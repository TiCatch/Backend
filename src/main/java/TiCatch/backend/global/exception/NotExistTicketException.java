package TiCatch.backend.global.exception;

public class NotExistTicketException extends RuntimeException {

  public NotExistTicketException() {
    super(ExceptionCode.NOT_EXIST_TICKET_EXCEPTION.getErrorMessage());
  }

  public NotExistTicketException(String message) {
    super(message);
  }
}
