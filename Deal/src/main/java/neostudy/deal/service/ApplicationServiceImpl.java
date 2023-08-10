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

    public Optional<Application> getApplicationById(Long id) {
        return applicationRepository.findById(id);
    }

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

    public Application findApplicationById(Long applicationId) {
        Optional<Application> application = getApplicationById(applicationId);
        if (application.isEmpty()) {
            throw new NotFoundException("Application " + applicationId + " not found");
        }
        if (application.get().getAppliedOffer() == null) {
            throw new NotFoundException("No applied offers for application " + application.get().getId());
        }
        return application.get();
    }

    public boolean isApplicationApprovedByConveyor(Application application) {
        return !(application.getStatus() == ApplicationStatus.PREAPPROVAL
                || application.getStatus() == ApplicationStatus.APPROVED);
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
