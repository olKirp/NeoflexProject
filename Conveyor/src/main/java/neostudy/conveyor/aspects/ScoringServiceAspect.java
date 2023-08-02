package neostudy.conveyor.aspects;

import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Configuration;

@Log4j2
@Aspect
@Configuration
public class ScoringServiceAspect {

    @Around("execution(* neostudy.conveyor.service.ScoringService+.*(..))")
    public Object logCreatedCredit(ProceedingJoinPoint point) throws Throwable {
        log.info("Creating credit for request: " + point.getArgs()[0]);
        Object o = point.proceed();
        log.info("Created credit: " + o);
        return o;
    }
}