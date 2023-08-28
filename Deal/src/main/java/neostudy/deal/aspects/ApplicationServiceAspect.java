package neostudy.deal.aspects;

import lombok.extern.log4j.Log4j2;
import neostudy.deal.dto.enums.ApplicationStatus;
import neostudy.deal.entity.Application;
import neostudy.deal.entity.Client;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Configuration;

@Log4j2
@Aspect
@Configuration
public class ApplicationServiceAspect {

    @Around("execution(* neostudy.deal.service.ApplicationService+.createApplicationForClient(..))")
    public Object createApplicationForClient(ProceedingJoinPoint point) throws Throwable {
        log.info("Creation of application for client with id " + ((Client) point.getArgs()[0]).getId() + " was started");
        Object o = point.proceed();
        log.info("Application was created: " + o);
        return o;
    }

    @Around("execution(* neostudy.deal.service.ApplicationService+.saveApplication(..))")
    public Object saveApplication(ProceedingJoinPoint point) throws Throwable {
        log.info("Save application to DB: " + point.getArgs()[0]);
        Object o = point.proceed();
        log.info("Application was saved with id " + ((Application) o).getId());
        return o;
    }

    @Around("execution(* neostudy.deal.service.ApplicationService+.isApplicationExists(..))")
    public Object isApplicationExists(ProceedingJoinPoint point) throws Throwable {
        log.info("Check if application exists for application with id " + point.getArgs()[0]);
        Object o = point.proceed();
        log.info("Application exists: " + o);
        return o;
    }

    @Around("execution(* neostudy.deal.service.ApplicationService+.isApplicationExistsByClientId(..))")
    public Object isApplicationExistsByClientId(ProceedingJoinPoint point) throws Throwable {
        log.info("Check if application exists. id: " + point.getArgs()[0]);
        Object o = point.proceed();
        log.info("Application exists: " + o);
        return o;
    }

    @Around("execution(* neostudy.deal.service.ApplicationService+.isApplicationApprovedByConveyor(..))")
    public Object isApplicationApprovedByConveyor(ProceedingJoinPoint point) throws Throwable {
        log.info("Check if application approved by conveyor. id: " + ((Application) point.getArgs()[0]).getId());
        Object o = point.proceed();
        log.info("Application approved: " + o);
        return o;
    }

    @Around("execution(* neostudy.deal.service.ApplicationService+.setLoanOfferToApplication(..))")
    public Object setLoanOfferToApplication(ProceedingJoinPoint point) throws Throwable {
        log.info("Set approved loan offer for application with id " + ((Application) point.getArgs()[0]).getId());
        return point.proceed();
    }

    @Around("execution(* neostudy.deal.service.ApplicationService+.setApplicationStatus(..))")
    public Object setApplicationStatus(ProceedingJoinPoint point) throws Throwable {
        log.info("Set application status " + ( point.getArgs()[1])
                + ", change type " + (point.getArgs()[2])
                + " for application with id " + ((Application) point.getArgs()[0]).getId());
        return point.proceed();
    }

}