package neostudy.gateway.exceptionhandler;

import feign.FeignException;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Log4j2
@RestControllerAdvice
public class ExceptionHandler {

    @org.springframework.web.bind.annotation.ExceptionHandler(FeignException.class)
    public ResponseEntity<String> FeignException(FeignException exception) {
        if (exception.status() == 409 || exception.status() == 404 || exception.status() == 400 || exception.status() == 500) {
            return ResponseEntity
                    .status(exception.status())
                    .body(exception.contentUTF8());
        } else {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("External microservice unavailable");
        }
    }

}
