package neostudy.dossier.aspects;

import lombok.extern.log4j.Log4j2;
import neostudy.dossier.dto.ApplicationDTO;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Configuration;

@Log4j2
@Aspect
@Configuration
public class DocumentsGeneratorAspect {
    @Around("execution(* neostudy.dossier.services.DocumentsGeneratorService+.*(..))")
    public Object createDocuments(ProceedingJoinPoint point) throws Throwable {
        log.info("Creation of document for application " + ((ApplicationDTO) point.getArgs()[0]).getId() + " was started");
        Object o = point.proceed();
        log.info("Document was created: " + o);
        return o;
    }
}
