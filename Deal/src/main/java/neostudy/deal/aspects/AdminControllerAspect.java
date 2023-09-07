package neostudy.deal.aspects;

import lombok.extern.log4j.Log4j2;
import neostudy.deal.dto.ApplicationDTO;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

@Log4j2
@Aspect
@Configuration
public class AdminControllerAspect {

    @Around("execution(* neostudy.deal.controller.AdminController.getApplicationById(..))")
    public Object getApplicationById(ProceedingJoinPoint point) throws Throwable {
        log.info("Received request for application with id " + point.getArgs()[0] + ".");
        ResponseEntity<ApplicationDTO> o = (ResponseEntity<ApplicationDTO>) point.proceed();
        if (o.getStatusCode() == HttpStatusCode.valueOf(200)) {
            log.info("Application " + point.getArgs()[0] + " was successfully returned");
        }
        return o;
    }
}
