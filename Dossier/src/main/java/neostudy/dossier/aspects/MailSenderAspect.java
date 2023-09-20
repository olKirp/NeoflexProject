package neostudy.dossier.aspects;

import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Log4j2
@Aspect
@Configuration
public class MailSenderAspect {
    @Around("execution(* neostudy.dossier.services.MailSenderService+.sendEmail(..))")
    public Object sendEmail(ProceedingJoinPoint point) throws Throwable {
        log.info("Send mail to: " + point.getArgs()[0] + ", subject: " + point.getArgs()[1] + ", message body: \"" + point.getArgs()[2] + "\"");
        return point.proceed();
    }

    @Around("execution(* neostudy.dossier.services.MailSenderService+.sendEmailWithAttachment(..))")
    public Object sendEmailWithAttachment(ProceedingJoinPoint point) throws Throwable {
        List<String> attachments = (List<String>) point.getArgs()[3];
        String logMsg = "Send mail to: " + point.getArgs()[0] + ", subject: " + point.getArgs()[1] + ", message body: \"" + point.getArgs()[2] + "\"" + ", attachments: ";
        for (String attachment : attachments) {
            logMsg = logMsg.concat(attachment).concat(" ");
        }
        log.info(logMsg);
        return point.proceed();
    }

}
