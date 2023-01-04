package antifraud.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class InvalidTransactionAmountException extends RuntimeException{
    public InvalidTransactionAmountException(String errorMessage) {
        super(errorMessage);
    }
}
