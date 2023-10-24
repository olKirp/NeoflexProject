package neostudy.deal.aspects;

import lombok.extern.log4j.Log4j2;
import neostudy.deal.entity.Application;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Configuration;

@Log4j2
@Aspect
@Configuration
public class FinishRegistrationServiceAspect {

    @Around("execution(* neostudy.deal.service.FinishRegistrationServiceImpl.mapDTOsToScoringData(..))")
    public Object mapToScoringData(ProceedingJoinPoint point) throws Throwable {
        log.info("Creating scoring data DTO for application " + ((Application)point.getArgs()[2]).getId());
        Object o = point.proceed();
        log.info("ScoringDataDTO was created: " + o);
        return o;
    }
}
