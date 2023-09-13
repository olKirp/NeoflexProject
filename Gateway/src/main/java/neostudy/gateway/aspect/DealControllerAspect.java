package neostudy.gateway.aspect;

import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Configuration;

@Log4j2
@Aspect
@Configuration
public class DealControllerAspect {
    @Around("execution(* neostudy.gateway.controller.GatewayDealController.sendDocuments(..))")
    public Object sendDocuments(ProceedingJoinPoint point) throws Throwable {
        log.info("Request to /gateway/document/" + (point.getArgs()[0]) + "/send");
        return point.proceed();
    }

    @Around("execution(* neostudy.gateway.controller.GatewayDealController.signDocumentsRequest(..))")
    public Object signDocumentsRequest(ProceedingJoinPoint point) throws Throwable {
        log.info("Request to /gateway/document/" + (point.getArgs()[0]) + "/sign");
        return point.proceed();
    }

    @Around("execution(* neostudy.gateway.controller.GatewayDealController.signDocuments(..))")
    public Object signDocuments(ProceedingJoinPoint point) throws Throwable {
        log.info("Request to /gateway/document/" + (point.getArgs()[0]) + "/code SES-code:" + (point.getArgs()[1]));
        return point.proceed();
    }

    @Around("execution(* neostudy.gateway.controller.GatewayDealController.createCredit(..))")
    public Object createCredit(ProceedingJoinPoint point) throws Throwable {
        log.info("Request to /gateway/deal/calculate/" + (point.getArgs()[1]) + " " + (point.getArgs()[0]));
        return point.proceed();
    }
}
