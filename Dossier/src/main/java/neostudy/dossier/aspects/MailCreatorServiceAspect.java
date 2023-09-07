package neostudy.dossier.aspects;

import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Configuration;

@Log4j2
@Aspect
@Configuration
public class MailCreatorServiceAspect {
    @Around("execution(* neostudy.dossier.services.MailCreatorService+.createMailBody(..))")
    public Object createMailBody(ProceedingJoinPoint point) throws Throwable {
        log.info("Creation of mail body for application " + point.getArgs()[1] + " and topic " + point.getArgs()[0] + " was started");
        Object o = point.proceed();
        log.info("Mail body created: \"" + o + "\"");
        return o;
    }
}
