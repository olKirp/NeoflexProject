package neostudy.deal.service;

import lombok.RequiredArgsConstructor;
import neostudy.deal.dto.ApplicationStatus;
import neostudy.deal.dto.enums.ChangeType;
import neostudy.deal.dto.Theme;
import neostudy.deal.entity.Application;
import neostudy.deal.exceptions.IncorrectApplicationStatusException;
import neostudy.deal.exceptions.IncorrectSesCodeException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class DocumentsServiceImpl implements DocumentsService {

    private final KafkaMessageSenderService msgSender;

    private final ApplicationService applicationService;

    public void sendDocuments(Long applicationId) {
        Application application = applicationService.getApplicationById(applicationId);
        checkApplication(application, ApplicationStatus.CC_APPROVED, applicationId);
        setApplicationStatus(application, ApplicationStatus.PREPARE_DOCUMENTS);
        msgSender.sendMessage(application.getClient().getEmail(), Theme.SEND_DOCUMENTS, applicationId);
    }

    public void signDocumentsRequest(Long applicationId) {
        Application application = applicationService.getApplicationById(applicationId);
        checkApplication(application, ApplicationStatus.DOCUMENTS_CREATED, applicationId);
        msgSender.sendMessage(application.getClient().getEmail(), Theme.SEND_SES, applicationId);
    }

    public void signDocuments(Long applicationId, String sesCode) {
        Application application = applicationService.getApplicationById(applicationId);
        checkApplication(application, ApplicationStatus.DOCUMENTS_CREATED, applicationId);
        if (!application.getSesCode().equals(sesCode)) {
            throw new IncorrectSesCodeException("The SES-code doesn't match application's " + applicationId + " SES-code");
        }
        setApplicationStatus(application, ApplicationStatus.DOCUMENT_SIGNED);

        msgSender.sendMessage(application.getClient().getEmail(), Theme.CREDIT_ISSUED, applicationId);

        application.setSignDate(LocalDate.now());
        setApplicationStatus(application, ApplicationStatus.CREDIT_ISSUED);
    }

    private void checkApplication(Application application, ApplicationStatus expectedStatus, Long applicationId) {
        if (application.getStatus() != expectedStatus) {
            throw new IncorrectApplicationStatusException("Application with id " + applicationId + " must have status " + expectedStatus + ", but has status " + application.getStatus());
        }
    }

    private void setApplicationStatus(Application application, ApplicationStatus status) {
        applicationService.setApplicationStatus(application, status, ChangeType.AUTOMATIC);
        applicationService.saveApplication(application);
    }
}
