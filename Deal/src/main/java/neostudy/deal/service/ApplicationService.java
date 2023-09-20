package neostudy.deal.service;

import neostudy.deal.dto.LoanOfferDTO;
import neostudy.deal.dto.ApplicationStatus;
import neostudy.deal.dto.ChangeType;
import neostudy.deal.entity.Application;
import neostudy.deal.entity.Client;

import java.util.List;
import java.util.Optional;

public interface ApplicationService {

    Optional<Application> findApplicationById(Long applicationId);

    void setApplicationStatus(Application application, ApplicationStatus status, ChangeType type);

    Application getApplicationForClient(Client client);

    Application saveApplication(Application application);

    void setLoanOfferToApplication(Application application, LoanOfferDTO appliedOffer);

    boolean isApplicationApprovedByConveyor(Application application);

    List<Application> getApplications();
}
