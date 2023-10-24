package neostudy.deal.aspects;

import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Configuration;

@Log4j2
@Aspect
@Configuration
public class MessageSenderAspect {

    @Around("execution(* neostudy.deal.service.KafkaMessageSenderService+.sendMessage(..))")
    public Object sendMessage(ProceedingJoinPoint point) throws Throwable {
        log.info("Sending message with address " + point.getArgs()[0] + ", theme \"" + point.getArgs()[1] + "\", application's id " + point.getArgs()[2] + " was started");
        return point.proceed();
    }
}
