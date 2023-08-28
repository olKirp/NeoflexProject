package neostudy.deal.aspects;

import lombok.extern.log4j.Log4j2;
import neostudy.deal.entity.Client;
import neostudy.deal.entity.Credit;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Configuration;

@Log4j2
@Aspect
@Configuration
public class CreditServiceAspect {

    @Around("execution(* neostudy.deal.service.CreditService+.saveCredit(..))")
    public Object saveCredit(ProceedingJoinPoint point) throws Throwable {
        log.info("Save credit to DB: " + point.getArgs()[0]);
        Object o = point.proceed();
        log.info("Credit was saved with id " + ((Client)o).getId());
        return o;
    }

    @Around("execution(* neostudy.deal.service.CreditService+.createCreditFromCreditDTO(..))")
    public Object createCreditFromCreditDTO(ProceedingJoinPoint point) throws Throwable {
        log.info("Create Credit from CreditDTO:" + point.getArgs()[0]);
        return point.proceed();
    }
}
