package neostudy.deal.aspects;

import lombok.extern.log4j.Log4j2;
import neostudy.deal.entity.Client;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Configuration;

@Log4j2
@Aspect
@Configuration
public class ClientServiceAspect {

    @Around("execution(* neostudy.deal.service.ClientService+.createClientForLoanRequest(..))")
    public Object createClientForLoanRequest(ProceedingJoinPoint point) throws Throwable {
        log.info("Creation of client for loan request was started");
        Object o = point.proceed();
        log.info("Client was created: " + o);
        return o;
    }

    @Around("execution(* neostudy.deal.service.ClientService+.saveClient(..))")
    public Object saveClient(ProceedingJoinPoint point) throws Throwable {
        log.info("Save client to DB: " + point.getArgs()[0]);
        Object o = point.proceed();
        log.info("Client was saved with id " + ((Client)o).getId());
        return o;
    }

    @Around("execution(* neostudy.deal.service.ClientService+.addInfoToClient(..))")
    public Object addInfoToClient(ProceedingJoinPoint point) throws Throwable {
        log.info("Add info " + point.getArgs()[1]
                +" to client with id " + ((Client)point.getArgs()[0]).getId());
        return point.proceed();
    }

    @Around("execution(* neostudy.deal.service.ClientService+.isClientExistByINN(..))")
    public Object isClientExistByINN(ProceedingJoinPoint point) throws Throwable {
        log.info("Check if client with inn " + point.getArgs()[0]
                + " exists");
        Object o = point.proceed();
        log.info("Client exists: " + o);
        return o;
    }

    @Around("execution(* neostudy.deal.service.ClientService+.isClientExistsByAccount(..))")
    public Object isClientExistsByAccount(ProceedingJoinPoint point) throws Throwable {
        log.info("Check if client with account " + point.getArgs()[0]
                + " exists");
        Object o = point.proceed();
        log.info("Client exists: " + o);
        return o;
    }
}
