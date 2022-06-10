package id.holigo.services.holigotrainservice.web.exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class FinalFareBadRequestException extends RuntimeException {

    public FinalFareBadRequestException() {
        super();
    }

    public FinalFareBadRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    public FinalFareBadRequestException(String message) {
        super(message);
    }

    public FinalFareBadRequestException(Throwable cause) {
        super(cause);
    }
}
