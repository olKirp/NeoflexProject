package neostudy.gateway.aspect;

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
    @Around("execution(* neostudy.gateway.exceptionhandler.ExceptionHandler.FeignException(..))")
    public Object FeignException(ProceedingJoinPoint point) throws Throwable {
        log.error(((FeignException)point.getArgs()[0]).getMessage());
        return point.proceed();
    }
}
