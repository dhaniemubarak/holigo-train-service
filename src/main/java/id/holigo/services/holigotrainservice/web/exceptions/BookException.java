package id.holigo.services.holigotrainservice.web.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class BookException extends RuntimeException {

    public BookException() {
        super();
    }

    public BookException(String message, Throwable cause) {
        super(message, cause);
    }

    public BookException(String message) {
        super(message);
    }

    public BookException(Throwable cause) {
        super(cause);
    }

    public BookException(String message, Throwable cause,
                         boolean enableSuppression,
                         boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
