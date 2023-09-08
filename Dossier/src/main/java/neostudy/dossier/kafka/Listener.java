package neostudy.dossier.kafka;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import neostudy.dossier.dto.ApplicationDTO;
import neostudy.dossier.dto.EmailMessageDTO;
import neostudy.dossier.dto.enums.ApplicationStatus;
import neostudy.dossier.exceptions.DealMicroserviceException;
import neostudy.dossier.feignclient.DealAPIClient;
import neostudy.dossier.services.DocumentsGeneratorService;
import neostudy.dossier.services.MailCreatorService;
import neostudy.dossier.services.MailSenderService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.MailSendException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Log4j2
@Component
@RequiredArgsConstructor
@Slf4j
public class Listener {

    private final MailCreatorService mailCreatorService;

    private final MailSenderService mailSender;

    private final DealAPIClient apiClient;

    private final DocumentsGeneratorService documentsGenerator;

    @KafkaListener(topics = "finish-registration")
    public void handleFinishRegistration(EmailMessageDTO message) {
        String body = mailCreatorService.createMailBody(message.getTheme(), message.getApplicationId());
        mailSender.sendEmail(message.getAddress(), "Finish registration", body);
    }

    @KafkaListener(topics = "create-documents")
    public void handleCreateDocuments(EmailMessageDTO message) {
        String body = mailCreatorService.createMailBody(message.getTheme(), message.getApplicationId());
        mailSender.sendEmail(message.getAddress(), "Application was approved", body);
    }

    @KafkaListener(topics = "send-documents")
    public void handleSendDocuments(EmailMessageDTO message) throws MessagingException {
        ApplicationDTO application;
        try {
            application = apiClient.getApplicationById(message.getApplicationId()).getBody();
            List<String> attachments = documentsGenerator.generateAllDocuments(application);
            String body = mailCreatorService.createMailBody(message.getTheme(), message.getApplicationId());
            mailSender.sendEmailWithAttachment(message.getAddress(), "Documents", body, attachments);
            apiClient.setApplicationStatus(message.getApplicationId(), ApplicationStatus.DOCUMENTS_CREATED);
        } catch (IOException exception) {
            log.error("Exception during documents generation: " + exception.getMessage());
        } catch (MailSendException exception) {
            log.error("Mail sendException: " + exception.getMessage());
            apiClient.setApplicationStatus(message.getApplicationId(), ApplicationStatus.CC_APPROVED);
        } catch (DealMicroserviceException ignored) {
        }
    }

    @KafkaListener(topics = "send-ses")
    public void handleSendSes(EmailMessageDTO message) {
        try {
            String body = mailCreatorService.createMailBody(message.getTheme(), message.getApplicationId());
            mailSender.sendEmail(message.getAddress(), "SES-code", body);
        } catch (DealMicroserviceException ignored) {
        }
    }

    @KafkaListener(topics = "credit-issued")
    public void handleCreditIssued(EmailMessageDTO message) {
        String body = mailCreatorService.createMailBody(message.getTheme(), message.getApplicationId());
        mailSender.sendEmail(message.getAddress(), "Credit issued", body);
    }

    @KafkaListener(topics = "application-denied")
    public void handleApplicationDenied(EmailMessageDTO message) {
        String body = mailCreatorService.createMailBody(message.getTheme(), message.getApplicationId());
        mailSender.sendEmail(message.getAddress(), "Application denied", body);
    }
}
