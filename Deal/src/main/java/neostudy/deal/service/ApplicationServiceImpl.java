package neostudy.deal.service;

import lombok.RequiredArgsConstructor;
import neostudy.deal.dto.LoanOfferDTO;
import neostudy.deal.dto.ApplicationStatus;
import neostudy.deal.dto.enums.ChangeType;
import neostudy.deal.entity.Application;
import neostudy.deal.entity.Client;
import neostudy.deal.entity.StatusHistory;
import neostudy.deal.exceptions.NotFoundException;
import neostudy.deal.repository.ApplicationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import static neostudy.deal.dto.ApplicationStatus.*;

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
        setApplicationStatus(application, APPROVED, ChangeType.AUTOMATIC);
    }

    public boolean isApplicationExists(Long id) {
        return applicationRepository.existsById(id);
    }

    public boolean isApplicationExistsByClientId(Long id) {
        return applicationRepository.existsApplicationByClientId(id);
    }

    public Long saveApplication(Application application) {
        return applicationRepository.save(application).getId();
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

    @Override
    public List<Application> getApplications() {
        return applicationRepository.findAll();
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
                    .sesCode(generateSesCode())
                    .statusHistory(new StatusHistory(PREAPPROVAL, LocalDateTime.now(), ChangeType.AUTOMATIC))
                    .build();

        }
        return application;
    }

    private String generateSesCode() {
        int randomInt = ThreadLocalRandom.current().nextInt(1000, 10000);
        return String.valueOf(randomInt);
    }
}
