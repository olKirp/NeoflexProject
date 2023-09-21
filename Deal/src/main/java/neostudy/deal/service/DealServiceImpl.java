package neostudy.deal.service;

import feign.FeignException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import neostudy.deal.dto.*;
import neostudy.deal.dto.ApplicationStatus;
import neostudy.deal.dto.ChangeType;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class DealServiceImpl implements DealService {

    private final FinishRegistrationService finishRegistrationService;

    private final ApplicationService applicationService;

    private final ClientService clientService;

    private final CreditService creditService;

    private final ConveyorAPIClient conveyorAPIClient;

    private final KafkaMessageSenderService msgSender;

    public void sendMessage(Long applicationId, @NonNull Theme theme) {
        Application application = applicationService.findApplicationById(applicationId).orElseThrow(() -> new NotFoundException("Application " + applicationId + " not found"));
        msgSender.sendMessage(application.getClient().getEmail(), theme, applicationId);
    }

    public List<LoanOfferDTO> createLoanOffers(@NonNull LoanApplicationRequestDTO loanRequest) {
        checkLoanRequest(loanRequest);

        Application application = applicationService.getApplicationForClient(clientService.createClientForLoanRequest(loanRequest));
        List<LoanOfferDTO> offers = getLoanOffersFromConveyor(loanRequest);
        setApplicationIdToOffers(offers, applicationService.saveApplication(application).getId());

        return offers;
    }

    private void checkLoanRequest(LoanApplicationRequestDTO loanRequest) {
        Optional<Client> optionalClient =
                clientService.findClientByPassportSeriesAndPassportNumber(loanRequest.getPassportSeries(), loanRequest.getPassportNumber());

        if (optionalClient.isPresent()) {
            Client client = optionalClient.get();
            if (client.getApplication().getAppliedOffer() != null) {
                throw new IncorrectApplicationStatusException("Client with passport " + client.getPassport().getSeries() + " " + client.getPassport().getNumber() + " already exists and approved application. Application cannot be changed");
            }
            if (!Objects.equals(loanRequest.getEmail(), client.getEmail()) && clientService.existsClientByEmail(loanRequest.getEmail())) {
                throw new UniqueConstraintViolationException("Another client has email " + loanRequest.getEmail());
            }
        } else {
            if (clientService.existsClientByEmail(loanRequest.getEmail())) {
                throw new UniqueConstraintViolationException("Another client has email " + loanRequest.getEmail());
            }
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

    public void approveLoanOffer(@NonNull LoanOfferDTO appliedOffer) {
        Application application = applicationService.findApplicationById(appliedOffer.getApplicationId())
                .orElseThrow(() -> new NotFoundException("Application " + appliedOffer.getApplicationId() + " not found"));

        if (applicationService.isApplicationApprovedByConveyor(application)) {
            throw new IncorrectApplicationStatusException("Application " + application.getId() + " has status " + application.getStatus() + " and cannot be changed");
        }

        applicationService.setLoanOfferToApplication(application, appliedOffer);
        applicationService.saveApplication(application);
    }

    public void createCreditForApplication(@NonNull FinishRegistrationRequestDTO registrationRequest, Long applicationId) {
        Application application = applicationService.findApplicationById(applicationId).orElseThrow(() -> new NotFoundException("Application " + applicationId + " not found"));

        if (application.getAppliedOffer() == null) {
            throw new NotFoundException("No applied offers for application " + application.getId());
        } else if (applicationService.isApplicationApprovedByConveyor(application)) {
            throw new IncorrectApplicationStatusException("Application " + application.getId() + " has status " + application.getStatus() + " and cannot be changed");
        }

        addInfoToClient(application.getClient(), registrationRequest);

        Credit credit = creditService.mapCreditDTOToCredit(getCreditFromConveyor(registrationRequest, application));

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
        clientService.mapFinishRegistrationRequestToClient(client, registrationRequest);
        clientService.saveClient(client);
    }

    private ScoringDataDTO mapToScoringData(FinishRegistrationRequestDTO request, Application application) {
        return finishRegistrationService.mapDTOsToScoringData(request, application.getClient(), application);
    }

    public void setAndSaveApplicationStatus(Long appId, @NonNull ApplicationStatus status, @NonNull ChangeType type) {
        Application application = applicationService.findApplicationById(appId).orElseThrow(() -> new NotFoundException("Application " + appId + " not found"));
        applicationService.setApplicationStatus(application, status, type);
        applicationService.saveApplication(application);
    }
}
