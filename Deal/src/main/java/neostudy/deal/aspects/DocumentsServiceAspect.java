package neostudy.deal.aspects;

import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Configuration;

@Log4j2
@Aspect
@Configuration
public class DocumentsServiceAspect {

    @Around("execution(* neostudy.deal.service.DocumentsService+.sendDocuments(..))")
    public Object sendDocuments(ProceedingJoinPoint point) throws Throwable {
        log.info("Sending message with request to send documents to client for application " + point.getArgs()[0] + " was started");
        return point.proceed();
    }

    @Around("execution(* neostudy.deal.service.DocumentsService+.signDocumentsRequest(..))")
    public Object signDocumentsRequest(ProceedingJoinPoint point) throws Throwable {
        log.info("Sending message with request to send SES-code to client for application " + point.getArgs()[0] + " was started");
        return point.proceed();
    }

    @Around("execution(* neostudy.deal.service.DocumentsService+.signDocuments(..))")
    public Object signDocuments(ProceedingJoinPoint point) throws Throwable {
        log.info("Signing documents with SES-code " + point.getArgs()[1] + " for application " + point.getArgs()[0] + " was started");
        return point.proceed();
    }
}
