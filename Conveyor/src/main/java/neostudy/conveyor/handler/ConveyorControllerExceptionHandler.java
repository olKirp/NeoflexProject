package neostudy.conveyor.handler;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Log4j2
@RestControllerAdvice
public class ConveyorControllerExceptionHandler {

    @ExceptionHandler({ IllegalArgumentException.class })
    public ResponseEntity<String> handleException(RuntimeException exception) {
        log.info("Loan application was rejected. Reason: " + exception.getMessage());
        return ResponseEntity.badRequest().body(exception.getMessage());
    }
}