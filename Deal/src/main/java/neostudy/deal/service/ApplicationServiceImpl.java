package neostudy.deal.service;

import lombok.RequiredArgsConstructor;
import neostudy.deal.dto.LoanOfferDTO;
import neostudy.deal.dto.enums.ApplicationStatus;
import neostudy.deal.dto.enums.ChangeType;
import neostudy.deal.entity.Application;
import neostudy.deal.entity.Client;
import neostudy.deal.entity.StatusHistory;
import neostudy.deal.exceptions.NotFoundException;
import neostudy.deal.repository.ApplicationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static neostudy.deal.dto.enums.ApplicationStatus.PREAPPROVAL;

@Service
@RequiredArgsConstructor
public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationRepository applicationRepository;

    public void setApplicationStatus(Application application, ApplicationStatus status, ChangeType type) {
        application.setStatus(status);
        StatusHistory statusHistory = StatusHistory.builder()
                .status(status)
                .time(LocalDateTime.now())
                .changeType(type)
                .build();
        application.setStatusHistory(statusHistory);
    }

    public void setLoanOfferToApplication(Application application, LoanOfferDTO appliedOffer) {
        application.setAppliedOffer(appliedOffer);
        setApplicationStatus(application, ApplicationStatus.APPROVED, ChangeType.AUTOMATIC);
    }

    public boolean isApplicationExists(Long id) {
        return applicationRepository.existsById(id);
    }

    public boolean isApplicationExistsByClientId(Long id) {
        return applicationRepository.existsApplicationByClientId(id);
    }

    public Application saveApplication(Application application) {
        return applicationRepository.save(application);
    }

    public Application getApplicationById(Long applicationId) {
        Optional<Application> application = applicationRepository.findById(applicationId);
        if (application.isEmpty()) {
            throw new NotFoundException("Application " + applicationId + " not found");
        }
        return application.get();
    }

    public boolean checkIfAppliedOfferExists(Application application) {
        return application.getAppliedOffer() != null;
    }

    public boolean isApplicationApprovedByConveyor(Application application) {
        return !(application.getStatus() == ApplicationStatus.PREAPPROVAL
                || application.getStatus() == ApplicationStatus.APPROVED
                || application.getStatus() == ApplicationStatus.CLIENT_DENIED
                || application.getStatus() == ApplicationStatus.CC_DENIED);
    }

    public Application createApplicationForClient(Client client) {
        Application application;
        if (isApplicationExistsByClientId(client.getId())) {
            application = applicationRepository.findApplicationByClientId(client.getId());
        } else {
            application = Application.builder()
                    .client(client)
                    .creationDate(LocalDate.now())
                    .status(PREAPPROVAL)
                    .signDate(LocalDate.now())
                    .build();
        }
        return application;
    }
}
