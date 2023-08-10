package neostudy.deal.controller;

import neostudy.deal.exceptions.ApplicationAlreadyApprovedException;
import neostudy.deal.exceptions.EntityAlreadyExistsException;
import neostudy.deal.exceptions.NotFoundException;
import org.modelmapper.spi.ErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class DealControllerExceptionHandler {

    @ExceptionHandler({ApplicationAlreadyApprovedException.class,
            EntityAlreadyExistsException.class})
    public ResponseEntity<ErrorMessage> applicationAlreadyApprovedException(Exception exception) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorMessage(exception.getMessage()));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorMessage> notFoundException(NotFoundException exception) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorMessage(exception.getMessage()));
    }
}
