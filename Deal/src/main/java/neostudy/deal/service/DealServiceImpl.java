package neostudy.deal.service;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import neostudy.deal.dto.*;
import neostudy.deal.dto.ApplicationStatus;
import neostudy.deal.dto.enums.ChangeType;
import neostudy.deal.entity.Application;
import neostudy.deal.entity.Client;
import neostudy.deal.entity.Credit;
import neostudy.deal.exceptions.IncorrectApplicationStatusException;
import neostudy.deal.exceptions.CreditConveyorDeniedException;
import neostudy.deal.exceptions.NotFoundException;
import neostudy.deal.exceptions.UniqueConstraintViolationException;
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

    private final KafkaMessageSenderService msgSender;

    public void sendMessage(Long applicationId, Theme theme) {
        Application application = applicationService.getApplicationById(applicationId);
        msgSender.sendMessage(application.getClient().getEmail(), theme, applicationId);
    }

    public List<LoanOfferDTO> createLoanOffera(LoanApplicationRequestDTO loanRequest) {
        checkLoanOffer(loanRequest);

        Application application = applicationService.createApplicationForClient(clientService.createClientForLoanRequest(loanRequest));
        application.setId(applicationService.saveApplication(application));

        List<LoanOfferDTO> offers = getLoanOffersFromConveyor(loanRequest);
        setApplicationIdToOffers(offers, application.getId());

        return offers;
    }

    private void checkLoanOffer(LoanApplicationRequestDTO loanRequest) {
        Client client = clientService.findClientByPassportSeriesAndPassportNumber(loanRequest.getPassportSeries(), loanRequest.getPassportNumber());
        if (client != null) {
            if (applicationService.checkIfAppliedOfferExists(client.getApplication())) {
                throw new IncorrectApplicationStatusException("Client with passport " + client.getPassport().getSeries() + " " + client.getPassport().getNumber() + " already exists and approved application. Application cannot be changed");
            }
            if (clientService.existsClientByEmail(loanRequest.getEmail()) && !clientService.getClientIdByEmail(loanRequest.getEmail()).equals(client.getId())) {
                throw new UniqueConstraintViolationException("Another client has email " + loanRequest.getEmail());
            }
        } else if (clientService.existsClientByEmail(loanRequest.getEmail())) {
            throw new UniqueConstraintViolationException("Another client has email " + loanRequest.getEmail());
        }
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
            throw new IncorrectApplicationStatusException("Application " + application.getId() + " has status " + application.getStatus() + " and cannot be changed");
        }

        applicationService.setLoanOfferToApplication(application, appliedOffer);
        applicationService.saveApplication(application);
    }

    private Application findApplicationById(Long applicationId) {
        return applicationService.getApplicationById(applicationId);
    }

    private void checkApplication(Application application) {
        if (!applicationService.checkIfAppliedOfferExists(application)) {
            throw new NotFoundException("No applied offers for application " + application.getId());
        } else if (applicationService.isApplicationApprovedByConveyor(application)) {
            throw new IncorrectApplicationStatusException("Application " + application.getId() + " has status " + application.getStatus() + " and cannot be changed");
        }
    }

    public void createCreditForApplication(FinishRegistrationRequestDTO registrationRequest, Long applicationId) {
        Application application = findApplicationById(applicationId);
        checkApplication(application);

        addInfoToClient(application.getClient(), registrationRequest);

        Credit credit = creditService.createCreditFromCreditDTO(getCreditFromConveyor(registrationRequest, application));

        application.setCredit(credit);
        applicationService.setApplicationStatus(application, ApplicationStatus.CC_APPROVED, ChangeType.AUTOMATIC);
        applicationService.saveApplication(application);
    }

    private CreditDTO getCreditFromConveyor(FinishRegistrationRequestDTO registrationRequest, Application application) {
        ResponseEntity<CreditDTO> entity;
        try {
            entity = conveyorAPIClient.createCredit(mapToScoringData(registrationRequest, application));
        } catch (FeignException e) {
            if (e.status() == 400) {
                applicationService.setApplicationStatus(application, ApplicationStatus.CC_DENIED, ChangeType.AUTOMATIC);
                applicationService.saveApplication(application);
                msgSender.sendMessage(application.getClient().getEmail(), Theme.APPLICATION_DENIED, application.getId());
                throw new CreditConveyorDeniedException(e.contentUTF8());
            } else {
                throw e;
            }
        }
        return entity.getBody();
    }

    private void addInfoToClient(Client client, FinishRegistrationRequestDTO registrationRequest) {
        clientService.addInfoToClient(client, registrationRequest);
        clientService.saveClient(client);
    }

    private ScoringDataDTO mapToScoringData(FinishRegistrationRequestDTO request, Application application) {
        return finishRegistrationService.mapToScoringData(request, application.getClient(), application);
    }

    public void setApplicationStatus(Long appId, ApplicationStatus status, ChangeType type) {
        Application application = applicationService.getApplicationById(appId);
        applicationService.setApplicationStatus(application, status, type);
        applicationService.saveApplication(application);
    }
}
