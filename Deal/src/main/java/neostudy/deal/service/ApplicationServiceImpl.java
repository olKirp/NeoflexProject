package neostudy.deal.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import neostudy.deal.dto.LoanOfferDTO;
import neostudy.deal.dto.ApplicationStatus;
import neostudy.deal.dto.ChangeType;
import neostudy.deal.entity.Application;
import neostudy.deal.entity.Client;
import neostudy.deal.entity.StatusHistory;
import neostudy.deal.repository.ApplicationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import static neostudy.deal.dto.ApplicationStatus.*;

@Service
@RequiredArgsConstructor
@Transactional
public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationRepository applicationRepository;

    private final List<ApplicationStatus> approvedByConveyorStatuses = List.of(ApplicationStatus.PREAPPROVAL, ApplicationStatus.APPROVED, ApplicationStatus.CLIENT_DENIED, ApplicationStatus.CC_DENIED);

    public void setApplicationStatus(@NonNull Application application, @NonNull ApplicationStatus status, @NonNull ChangeType type) {
        application.setStatus(status);
        StatusHistory statusHistory = StatusHistory.builder()
                .status(status)
                .time(LocalDateTime.now())
                .changeType(type)
                .build();
        application.setStatusHistory(statusHistory);
    }

    public void setLoanOfferToApplication(@NonNull Application application, @NonNull LoanOfferDTO appliedOffer) {
        application.setAppliedOffer(appliedOffer);
        setApplicationStatus(application, APPROVED, ChangeType.AUTOMATIC);
    }

    public Application saveApplication(@NonNull Application application) {
        return applicationRepository.save(application);
    }

    public Optional<Application> findApplicationById(Long applicationId) {
        return applicationRepository.findById(applicationId);
    }

    public boolean isApplicationApprovedByConveyor(@NonNull Application application) {
        return !approvedByConveyorStatuses.contains(application.getStatus());
    }

    @Override
    public List<Application> getApplications() {
        return applicationRepository.findAll();
    }

    public Application getApplicationForClient(@NonNull Client client) {
        return applicationRepository.findApplicationByClientId(client.getId()).
                or(() -> Optional.ofNullable(createApplication(client))).get();
    }

    private Application createApplication(Client client) {
        return Application.builder()
                .client(client)
                .creationDate(LocalDate.now())
                .status(PREAPPROVAL)
                .sesCode(generateSesCode())
                .statusHistory(new StatusHistory(PREAPPROVAL, LocalDateTime.now(), ChangeType.AUTOMATIC))
                .build();
    }

    private String generateSesCode() {
        int randomInt = ThreadLocalRandom.current().nextInt(1000, 10000);
        return String.valueOf(randomInt);
    }

}
