package neostudy.deal.service;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import neostudy.deal.dto.*;
import neostudy.deal.dto.enums.ApplicationStatus;
import neostudy.deal.dto.enums.ChangeType;
import neostudy.deal.entity.Application;
import neostudy.deal.entity.Client;
import neostudy.deal.entity.Credit;
import neostudy.deal.exceptions.ApplicationAlreadyApprovedException;
import neostudy.deal.exceptions.CreditConveyorDeniedException;
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
        Client client = clientService.findClientByPassportSeriesAndPassportNumber(loanRequest.getPassportSeries(), loanRequest.getPassportNumber());
        if (client != null && applicationService.checkIfAppliedOfferExists(client.getApplication())) {
            throw new ApplicationAlreadyApprovedException("Client with id " + client.getId() + " already approved application. Application cannot be changed");
        }

        List<LoanOfferDTO> offers = getLoanOffersFromConveyor(loanRequest);

        Application application = createApplicationAndClientForLoanRequest(loanRequest);
        applicationService.setApplicationStatus(application, ApplicationStatus.PREAPPROVAL, ChangeType.AUTOMATIC);
        application = applicationService.saveApplication(application);
        setApplicationIdToOffers(offers, application.getId());

        return offers;
    }

    private List<LoanOfferDTO> getLoanOffersFromConveyor(LoanApplicationRequestDTO loanRequest) {
        try {
            return conveyorAPIClient.createLoanOffers(loanRequest).getBody();
        } catch (FeignException e) {
            if (e.status() == 400) {
                throw new CreditConveyorDeniedException(e.contentUTF8());
            } else {
                throw e;
            }
        }
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

    private Application createApplicationAndClientForLoanRequest(LoanApplicationRequestDTO loanRequest) {
        Client client = clientService.createClientForLoanRequest(loanRequest);
        clientService.saveClient(client);
        return applicationService.createApplicationForClient(client);
    }

    public void createCreditForApplication(FinishRegistrationRequestDTO registrationRequest, Long applicationId) {
        Application application = findApplicationById(applicationId);
        if (!applicationService.checkIfAppliedOfferExists(application)) {
            throw new NotFoundException("No applied offers for application " + application.getId());
        } else if (applicationService.isApplicationApprovedByConveyor(application)) {
            throw new ApplicationAlreadyApprovedException("Application " + application.getId() + " has status " + application.getStatus() + " and cannot be changed");
        }

        addInfoToClient(application.getClient(), registrationRequest);

        ResponseEntity<CreditDTO> entity;
        try {
            entity = conveyorAPIClient.createCredit(mapToScoringData(registrationRequest, application));
        } catch (FeignException e) {
            if (e.status() == 400) {
                applicationService.setApplicationStatus(application, ApplicationStatus.CC_DENIED, ChangeType.AUTOMATIC);
                applicationService.saveApplication(application);
                throw new CreditConveyorDeniedException(e.contentUTF8());
            } else {
                throw e;
            }
        }

        Credit credit = creditService.createCreditFromCreditDTO(entity.getBody());

        application.setCredit(credit);
        applicationService.setApplicationStatus(application, ApplicationStatus.CC_APPROVED, ChangeType.AUTOMATIC);
        applicationService.saveApplication(application);
    }

    private void addInfoToClient(Client client, FinishRegistrationRequestDTO registrationRequest) {
        clientService.addInfoToClient(client, registrationRequest);
        clientService.saveClient(client);
    }

    private ScoringDataDTO mapToScoringData(FinishRegistrationRequestDTO request, Application application) {
        return finishRegistrationService.mapToScoringData(request, application.getClient(), application);
    }
}
