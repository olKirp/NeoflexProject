package neostudy.conveyor.controllers;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Log4j2
@ControllerAdvice
public class ConveyorControllerExceptionHandler {

    @ExceptionHandler({ IllegalArgumentException.class })
    public ResponseEntity<String> handleException(RuntimeException exception) {
        log.info("Loan application was rejected. Reason: " + exception.getMessage());
        return ResponseEntity.ok(exception.getMessage());
    }
}
