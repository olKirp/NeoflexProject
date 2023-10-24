package neostudy.deal.service;

import lombok.NonNull;
import neostudy.deal.dto.LoanOfferDTO;
import neostudy.deal.dto.ApplicationStatus;
import neostudy.deal.dto.ChangeType;
import neostudy.deal.entity.Application;
import neostudy.deal.entity.Client;

import java.util.List;
import java.util.Optional;

public interface ApplicationService {

    Optional<Application> findApplicationById(Long applicationId);

    void setApplicationStatus(@NonNull Application application, @NonNull ApplicationStatus status, @NonNull ChangeType type);

    Application getApplicationForClient(@NonNull Client client);

    Application saveApplication(@NonNull Application application);

    void setLoanOfferToApplication(@NonNull Application application, @NonNull LoanOfferDTO appliedOffer);

    boolean isApplicationApprovedByConveyor(@NonNull Application application);

    List<Application> getApplications();
}
