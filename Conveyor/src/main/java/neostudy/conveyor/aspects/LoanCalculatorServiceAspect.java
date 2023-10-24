package neostudy.conveyor.aspects;

import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Configuration;

@Log4j2
@Aspect
@Configuration
public class LoanCalculatorServiceAspect {

    @Around("execution(* neostudy.conveyor.service.LoanCalculatorService.calculateMonthlyPayment(..))")
    public Object logCalculateMonthlyPayment(ProceedingJoinPoint point) throws Throwable {
        log.info("Calculate monthly payment. Amount=" + point.getArgs()[0] + ", rate=" + point.getArgs()[1] + ", term=" + point.getArgs()[2]);
        Object o = point.proceed();
        log.info("Monthly payment = " + o);
        return o;
    }

    @Around("execution(* neostudy.conveyor.service.LoanCalculatorService.createPaymentSchedule(..))")
    public Object logCreatePaymentSchedule(ProceedingJoinPoint point) throws Throwable {
        log.info("Creating payment schedule. Amount=" + point.getArgs()[0] + ", rate=" + point.getArgs()[1] + ", term=" + point.getArgs()[2] + ", first payment=" + point.getArgs()[3]);
        Object o = point.proceed();
        log.info("Payment schedule = " + o);
        return o;
    }

    @Around("execution(* neostudy.conveyor.service.LoanCalculatorService.calculateAmountWithInsurance(..))")
    public Object logCalculateAmountWithInsurance(ProceedingJoinPoint point) throws Throwable {
        log.info("Calculating amount with insurance. isInsurance=" + point.getArgs()[0] + ", amount=" + point.getArgs()[1]);
        Object o = point.proceed();
        log.info("Amount with insurance = " + o);
        return o;
    }

    @Around("execution(* neostudy.conveyor.service.LoanCalculatorService.calculatePSK(..))")
    public Object logCalculatePSK(ProceedingJoinPoint point) throws Throwable {
        log.info("Calculating PSK. Amount=" + point.getArgs()[1] + ", term=" + point.getArgs()[2]);
        Object o = point.proceed();
        log.info("PSK = " + o);
        return o;
    }
}