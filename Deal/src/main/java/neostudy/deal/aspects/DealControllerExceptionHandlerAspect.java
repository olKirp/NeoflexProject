package neostudy.deal.aspects;

import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Configuration;

@Log4j2
@Aspect
@Configuration
public class DealControllerExceptionHandlerAspect {

    @Around("execution(* neostudy.deal.exceptionshandler.DealControllerExceptionHandler.*(..))")
    public Object catchException(ProceedingJoinPoint point) throws Throwable {
        log.info("Exception: " +  ((Exception)point.getArgs()[0]).getMessage());
        return point.proceed();
    }

}
