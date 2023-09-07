package neostudy.deal.service;

import neostudy.deal.dto.LoanOfferDTO;
import neostudy.deal.dto.ApplicationStatus;
import neostudy.deal.dto.enums.ChangeType;
import neostudy.deal.entity.Application;
import neostudy.deal.entity.Client;

public interface ApplicationService {

    boolean checkIfAppliedOfferExists(Application application);

    Application getApplicationById(Long applicationId);

    void setApplicationStatus(Application application, ApplicationStatus status, ChangeType type);

    Application createApplicationForClient(Client client);

    boolean isApplicationExists(Long id);

    Long saveApplication(Application application);

    void setLoanOfferToApplication(Application application, LoanOfferDTO appliedOffer);

    boolean isApplicationApprovedByConveyor(Application application);
}
