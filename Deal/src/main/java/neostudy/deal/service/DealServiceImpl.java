package neostudy.deal.service;

import lombok.RequiredArgsConstructor;
import neostudy.deal.dto.*;
import neostudy.deal.dto.enums.ApplicationStatus;
import neostudy.deal.dto.enums.ChangeType;
import neostudy.deal.entity.Application;
import neostudy.deal.entity.Client;
import neostudy.deal.entity.Credit;
import neostudy.deal.exceptions.ApplicationAlreadyApprovedException;
import neostudy.deal.exceptions.EntityAlreadyExistsException;
import neostudy.deal.exceptions.NotFoundException;
import neostudy.deal.feignclient.ConveyorAPIClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DealServiceImpl implements DealService {
    
    private final FinishRegistrationService finishRegistrationService;

    private final ApplicationService applicationService;

    private final ClientService clientService;

    private final CreditService creditService;

    private final ConveyorAPIClient conveyorAPIClient;

    public List<LoanOfferDTO> getLoanOffers(LoanApplicationRequestDTO loanRequest) {
        Application application = createApplicationForLoanRequest(loanRequest);

        List<LoanOfferDTO> offers = conveyorAPIClient.createLoanOffers(loanRequest).getBody();
        setApplicationIdToOffers(offers, application.getId());

        return offers;
    }

    private void setApplicationIdToOffers(List<LoanOfferDTO> offers, Long id) {
        offers.forEach(offer -> offer.setApplicationId(id));
    }

    public void approveLoanOffer(LoanOfferDTO appliedOffer) {
        if (!applicationService.isApplicationExists(appliedOffer.getApplicationId())) {
            throw new NotFoundException("Application " + appliedOffer.getApplicationId() + " not found");
        }

        Application application = applicationService.getApplicationById(appliedOffer.getApplicationId());

        if (applicationService.isApplicationApprovedByConveyor(application)) {
            throw new ApplicationAlreadyApprovedException("Application " + application.getId() + " has status " + application.getStatus() + " and cannot be changed");
        }

        applicationService.setLoanOfferToApplication(application, appliedOffer);
        applicationService.saveApplication(application);
    }

    private Application findApplicationById(Long applicationId) {
        return applicationService.getApplicationById(applicationId);
    }

    private Application createApplicationForLoanRequest(LoanApplicationRequestDTO loanRequest) {
        Client client = clientService.createClientForLoanRequest(loanRequest);

        Application application = applicationService.createApplicationForClient(client);
        return applicationService.saveApplication(application);
    }

    public void createCreditForApplication(FinishRegistrationRequestDTO registrationRequest, Long applicationId) {
        Application application = findApplicationById(applicationId);
        if (applicationService.isApplicationApprovedByConveyor(application)) {
            throw new ApplicationAlreadyApprovedException("Application " + application.getId() + " has status " + application.getStatus() + " and cannot be changed");
        }

        addInfoToClient(application.getClient(), registrationRequest);

        ResponseEntity<CreditDTO> entity = conveyorAPIClient.createCredit(mapToScoringData(registrationRequest, application));

        Credit credit = creditService.createCreditFromCreditDTO(entity.getBody());

        application.setCredit(credit);
        applicationService.setApplicationStatus(application, ApplicationStatus.CC_APPROVED, ChangeType.AUTOMATIC);
        applicationService.saveApplication(application);
    }

    private void addInfoToClient(Client client, FinishRegistrationRequestDTO registrationRequest) {
        if (clientService.isClientExistByINN(registrationRequest.getEmploymentDTO().getEmployerINN())) {
            throw new EntityAlreadyExistsException("Client with INN " + registrationRequest.getEmploymentDTO().getEmployerINN() + " already exists");
        }
        if (clientService.isClientExistsByAccount(registrationRequest.getAccount())) {
            throw new EntityAlreadyExistsException("Client with account " + registrationRequest.getAccount() + " already exists");
        }

        clientService.addInfoToClient(client, registrationRequest);
        clientService.saveClient(client);
    }

    private ScoringDataDTO mapToScoringData(FinishRegistrationRequestDTO request, Application application) {
        return finishRegistrationService.mapToScoringData(request, application.getClient(), application);
    }
}
