package neostudy.conveyor.aspects;

import lombok.extern.log4j.Log4j2;
import neostudy.conveyor.dto.LoanOfferDTO;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Log4j2
@Aspect
@Configuration
public class PrescoringServiceAspect {

    @Around("execution(* neostudy.conveyor.service.PrescoringService+.createLoanOffers(..))")
    public Object logCreateLoanOffers(ProceedingJoinPoint point) throws Throwable {
        log.info("Creating loan offers for request " + point.getArgs()[0] + " started");
        List<LoanOfferDTO> offers = (List<LoanOfferDTO>) point.proceed();
        log.info("Loan offers was created:\n"
                + offers.get(0) + "\n"
                + offers.get(1) + "\n"
                + offers.get(2) + "\n"
                + offers.get(3) + "\n");
        return offers;
    }

    @Around("execution(* neostudy.conveyor.service.PrescoringService+.calculateRate(..))")
    public Object logPrescoringService(ProceedingJoinPoint point) throws Throwable {
        log.info("Calculate rate in PrescoringService. IsSalaryClient=" + point.getArgs()[0] + ", IsInsurance=" + point.getArgs()[1] + ", base rate=" + point.getArgs()[2]);
        Object o = point.proceed();
        log.info("Calculated rate: " + o);
        return o;
    }
}
