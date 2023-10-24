package neostudy.deal.aspects;

import lombok.extern.log4j.Log4j2;
import neostudy.deal.dto.LoanOfferDTO;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Configuration;

@Log4j2
@Aspect
@Configuration
public class DealServiceAspect {

    @Around("execution(* neostudy.deal.service.DealService+.getLoanOffers(..))")
    public Object getLoanOffers(ProceedingJoinPoint point) throws Throwable {
        log.info("Loan offers creation for request " + point.getArgs()[0] + " was started");
        Object o = point.proceed();
        log.info("Offers was created: " + o);
        return o;
    }

    @Around("execution(* neostudy.deal.service.DealService+.approveLoanOffer(..))")
    public Object approveLoanOffer(ProceedingJoinPoint point) throws Throwable {
        log.info("Saving approved loan offer "
                + point.getArgs()[0]
                + " for application with id "
                + ((LoanOfferDTO)point.getArgs()[0]).getApplicationId() + " was started");
        return point.proceed();
    }

    @Around("execution(* neostudy.deal.service.DealService+.createCreditForApplication(..))")
    public Object createCreditForApplication(ProceedingJoinPoint point) throws Throwable {
        log.info("Credit creation for application with id "
                + point.getArgs()[1]
                + " was started");
        return point.proceed();
    }

    @Around("execution(* neostudy.deal.service.DealService+.sendMessage(..))")
    public Object sendMessage(ProceedingJoinPoint point) throws Throwable {
        log.info("Creating message for application " + point.getArgs()[0] + " was started. Topic: " + point.getArgs()[1]);
        return point.proceed();
    }
}
