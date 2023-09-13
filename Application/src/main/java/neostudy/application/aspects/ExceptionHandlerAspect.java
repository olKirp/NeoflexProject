package neostudy.application.aspects;

import feign.FeignException;
import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Configuration;

@Log4j2
@Aspect
@Configuration
public class ExceptionHandlerAspect {

    @Around("execution(* neostudy.application.exceptionshandler.ApplicationExceptionHandler.handleIllegalArgumentException(..))")
    public Object handleIllegalArgumentException(ProceedingJoinPoint point) throws Throwable {
        log.error("Exception: " + ((Exception) point.getArgs()[0]).getMessage());
        return point.proceed();
    }

    @Around("execution(* neostudy.application.exceptionshandler.ApplicationExceptionHandler.handleFeignException(..))")
    public Object handleFeignException(ProceedingJoinPoint point) throws Throwable {
        FeignException e = (FeignException) point.getArgs()[0];
        if (e.status() != 400 && e.status() != 409 && e.status() != 404) {
            log.error("Feign client exception. " + e.getMessage());
        } else {
            log.error("Exception from external service: " + e.getMessage());
        }

        return point.proceed();
    }
}
