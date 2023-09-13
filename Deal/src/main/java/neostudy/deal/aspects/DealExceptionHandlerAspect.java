package neostudy.deal.aspects;

import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Configuration;

@Log4j2
@Aspect
@Configuration
public class DealExceptionHandlerAspect {

    @Around("execution(* neostudy.deal.exceptionshandler.DealExceptionHandler.*(..))")
    public Object catchException(ProceedingJoinPoint point) throws Throwable {
        log.error("Exception: " +  ((Exception)point.getArgs()[0]).getMessage());
        return point.proceed();
    }
}
