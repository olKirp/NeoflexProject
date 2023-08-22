package neostudy.deal.exceptionshandler;

import feign.FeignException;
import neostudy.deal.exceptions.ApplicationAlreadyApprovedException;
import neostudy.deal.exceptions.CreditConveyorDeniedException;
import neostudy.deal.exceptions.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class DealExceptionHandler {

    @ExceptionHandler(ApplicationAlreadyApprovedException.class)
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
}
