package neostudy.application.aspects;

import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Configuration;

@Log4j2
@Aspect
@Configuration
public class PrescoringServiceAspect {

    @Around("execution(* neostudy.application.service.PrescoringService+.validateLoanRequest(..))")
    public Object validateLoanRequest(ProceedingJoinPoint point) throws Throwable {
        log.info("Validation of loan request: " + point.getArgs()[0] + " was started");
        Object o = point.proceed();
        log.info("Loan request is valid");
        return o;
    }

    @Around("execution(* neostudy.application.service.PrescoringService+.validateOffer(..))")
    public Object validateOffer(ProceedingJoinPoint point) throws Throwable {
        log.info("Validation of loan offer: " + point.getArgs()[0] + " was started");
        Object o = point.proceed();
        log.info("Loan offer is valid");
        return o;
    }
}