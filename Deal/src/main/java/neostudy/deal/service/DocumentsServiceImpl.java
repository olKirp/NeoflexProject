package neostudy.deal.service;

import lombok.RequiredArgsConstructor;
import neostudy.deal.dto.ApplicationStatus;
import neostudy.deal.dto.ChangeType;
import neostudy.deal.dto.Theme;
import neostudy.deal.entity.Application;
import neostudy.deal.exceptions.IncorrectApplicationStatusException;
import neostudy.deal.exceptions.IncorrectSesCodeException;
import neostudy.deal.exceptions.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Objects;

@Service
@Transactional
@RequiredArgsConstructor
public class DocumentsServiceImpl implements DocumentsService {

    private final KafkaMessageSenderService msgSender;

    private final ApplicationService applicationService;

    public void sendDocuments(Long applicationId) {
        Application application = applicationService.findApplicationById(applicationId).orElseThrow(() -> new NotFoundException("Application " + applicationId + " not found"));
        checkApplicationStatus(application, ApplicationStatus.CC_APPROVED, applicationId);
        applicationService.setApplicationStatus(application, ApplicationStatus.PREPARE_DOCUMENTS, ChangeType.AUTOMATIC);
        msgSender.sendMessage(application.getClient().getEmail(), Theme.SEND_DOCUMENTS, applicationId);
        applicationService.saveApplication(application);
    }

    public void signDocumentsRequest(Long applicationId) {
        Application application = applicationService.findApplicationById(applicationId).orElseThrow(() -> new NotFoundException("Application " + applicationId + " not found"));
        checkApplicationStatus(application, ApplicationStatus.DOCUMENTS_CREATED, applicationId);
        msgSender.sendMessage(application.getClient().getEmail(), Theme.SEND_SES, applicationId);
    }

    public void signDocuments(Long applicationId, String sesCode) {
        Application application = applicationService.findApplicationById(applicationId).orElseThrow(() -> new NotFoundException("Application " + applicationId + " not found"));
        checkApplicationStatus(application, ApplicationStatus.DOCUMENTS_CREATED, applicationId);
        if (!Objects.equals(application.getSesCode(), sesCode)) {
            throw new IncorrectSesCodeException("The SES-code doesn't match application's " + applicationId + " SES-code");
        }
        applicationService.setApplicationStatus(application, ApplicationStatus.DOCUMENT_SIGNED, ChangeType.AUTOMATIC);

        msgSender.sendMessage(application.getClient().getEmail(), Theme.CREDIT_ISSUED, applicationId);

        application.setSignDate(LocalDate.now());
        applicationService.setApplicationStatus(application, ApplicationStatus.CREDIT_ISSUED, ChangeType.AUTOMATIC);
        applicationService.saveApplication(application);
    }

    private void checkApplicationStatus(Application application, ApplicationStatus expectedStatus, Long applicationId) {
        if (application.getStatus() != expectedStatus) {
            throw new IncorrectApplicationStatusException("Application with id " + applicationId + " must have status " + expectedStatus + ", but has status " + application.getStatus());
        }
    }
}
