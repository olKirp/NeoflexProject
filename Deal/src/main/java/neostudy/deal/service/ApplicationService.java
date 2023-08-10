package neostudy.deal.service;

import neostudy.deal.dto.LoanOfferDTO;
import neostudy.deal.dto.enums.ApplicationStatus;
import neostudy.deal.dto.enums.ChangeType;
import neostudy.deal.entity.Application;
import neostudy.deal.entity.Client;

import java.util.Optional;

public interface ApplicationService {

    Optional<Application> getApplicationById(Long id);

    void setApplicationStatus(Application application, ApplicationStatus status, ChangeType type);

    Application findApplicationById(Long applicationId);

    Application createApplicationForClient(Client client);

    boolean isApplicationExists(Long id);

    boolean isApplicationExistsByClientId(Long id);

    Application saveApplication(Application application);

    void setLoanOfferToApplication(Application application, LoanOfferDTO appliedOffer);

    boolean isApplicationApprovedByConveyor(Application application);
}
