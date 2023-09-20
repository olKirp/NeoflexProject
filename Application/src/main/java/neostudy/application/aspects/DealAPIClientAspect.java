package neostudy.application.aspects;

import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.openapitools.api.ApplicationApi;
import org.springframework.context.annotation.Configuration;

@Log4j2
@Aspect
@Configuration
public class DealAPIClientAspect {

    @Around("execution(* neostudy.application.feignclient.DealAPIClient.createLoanOffers(..))")
    public Object createLoanOffers(ProceedingJoinPoint point) throws Throwable {
        log.info("Sent request to Deal: /deal/application");
        return point.proceed();
    }

    @Around("execution(* neostudy.application.feignclient.DealAPIClient.saveLoanOffer(..))")
    public Object saveLoanOffer(ProceedingJoinPoint point) throws Throwable {
        log.info("Sent request to Deal: /deal/offer");
        return point.proceed();
    }
}