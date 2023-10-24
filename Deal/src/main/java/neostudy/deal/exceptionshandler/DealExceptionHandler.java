package neostudy.deal.exceptionshandler;

import feign.FeignException;
import jakarta.validation.ConstraintViolationException;
import neostudy.deal.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class DealExceptionHandler {

    @ExceptionHandler({IncorrectApplicationStatusException.class, UniqueConstraintViolationException.class, IncorrectSesCodeException.class})
    public ResponseEntity<String> applicationAlreadyApprovedException(Exception exception) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(exception.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> notFoundException(NotFoundException exception) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(exception.getMessage());
    }

    @ExceptionHandler(CreditConveyorDeniedException.class)
    public ResponseEntity<String> creditConveyorDeniedException(CreditConveyorDeniedException exception) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body("Credit conveyor reject application. Reason: " + exception.getMessage());
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<String> FeignException(FeignException exception) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Credit conveyor unavailable");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> FeignException(IllegalArgumentException e) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(e.getMessage());
    }

    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<String> ConstraintViolationException(ConstraintViolationException exception) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(exception.getMessage());
    }
}
