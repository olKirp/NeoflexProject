package neostudy.gateway.aspect;

import lombok.extern.log4j.Log4j2;
import neostudy.gateway.dto.LoanOfferDTO;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Log4j2
@Aspect
@Configuration
public class ApplicationControllerAspect {
    @Around("execution(* neostudy.gateway.controller.GatewayApplicationController.createLoanOffers(..))")
    public Object createLoanOffers(ProceedingJoinPoint point) throws Throwable {
        log.info("Request to /gateway/application. Loan application request:" + (point.getArgs()[0]));
        ResponseEntity<List<LoanOfferDTO>> o = (ResponseEntity<List<LoanOfferDTO>>) point.proceed();

        log.info("Response:\n"
                + o);

        return o;
    }

    @Around("execution(* neostudy.gateway.controller.GatewayApplicationController.saveLoanOffer(..))")
    public Object saveLoanOffer(ProceedingJoinPoint point) throws Throwable {
        log.info("Request to /gateway/application/offer. Loan offer:" + (point.getArgs()[0]));
        return point.proceed();
    }
}
