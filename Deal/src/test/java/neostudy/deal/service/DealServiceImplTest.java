package neostudy.deal.service;

import neostudy.deal.dto.FinishRegistrationRequestDTO;
import neostudy.deal.dto.LoanApplicationRequestDTO;
import neostudy.deal.dto.LoanOfferDTO;
import neostudy.deal.dto.ApplicationStatus;
import neostudy.deal.dto.enums.ChangeType;
import neostudy.deal.entity.Application;
import neostudy.deal.entity.Client;
import neostudy.deal.exceptions.IncorrectApplicationStatusException;
import neostudy.deal.exceptions.NotFoundException;
import neostudy.deal.exceptions.UniqueConstraintViolationException;
import neostudy.deal.feignclient.ConveyorAPIClient;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DealServiceImplTest {

    private static DealServiceImpl dealService;
    
    private static final Application application = Instancio.create(Application.class);
    
    private static FinishRegistrationService finishRegistrationService;
    private static ApplicationService applicationService;
    private static ClientService clientService;
    private static CreditService creditService;
    private static ConveyorAPIClient conveyorAPIClient;
    private static KafkaMessageSenderService msgSender;

    @BeforeAll
    static void init() {
        finishRegistrationService = Mockito.mock();
        applicationService = Mockito.mock();
        clientService = Mockito.mock();
        creditService = Mockito.mock();
        conveyorAPIClient = Mockito.mock();
        msgSender = Mockito.mock();

        dealService = new DealServiceImpl(finishRegistrationService, applicationService, clientService, creditService, conveyorAPIClient, msgSender);

        Mockito.when(applicationService.getApplicationById(application.getId())).thenReturn(application);
    }

    @Test
    void approveLoanOffer() {
        Mockito.when(applicationService.isApplicationExists(application.getId())).thenReturn(false);

        LoanOfferDTO loanOffer = new LoanOfferDTO();
        loanOffer.setApplicationId(application.getId());

        Exception exception = assertThrows(NotFoundException.class, () -> dealService.approveLoanOffer(loanOffer));
        assertEquals("Application " + application.getId() + " not found", exception.getMessage());

        Mockito.when(applicationService.isApplicationExists(application.getId())).thenReturn(true);

        Mockito.when(applicationService.isApplicationApprovedByConveyor(application)).thenReturn(true);
        exception = assertThrows(IncorrectApplicationStatusException.class, () -> dealService.approveLoanOffer(loanOffer));
        assertEquals("Application " + application.getId() + " has status " + application.getStatus() + " and cannot be changed", exception.getMessage());
    }

    @Test
    void createCreditForApplication() {
        Mockito.when(applicationService.checkIfAppliedOfferExists(application)).thenReturn(false);

        Exception exception = assertThrows(NotFoundException.class, () -> dealService.createCreditForApplication(new FinishRegistrationRequestDTO(), application.getId()));
        assertEquals("No applied offers for application " + application.getId(), exception.getMessage());

        Mockito.when(applicationService.checkIfAppliedOfferExists(application)).thenReturn(true);
        Mockito.when(applicationService.isApplicationApprovedByConveyor(application)).thenReturn(true);
        exception = assertThrows(IncorrectApplicationStatusException.class, () -> dealService.createCreditForApplication(new FinishRegistrationRequestDTO(), application.getId()));
        assertEquals("Application " + application.getId() + " has status " + application.getStatus() + " and cannot be changed", exception.getMessage());
    }

    @Test
    void setApplicationStatus() {
        application.setStatus(ApplicationStatus.PREAPPROVAL);
        assertDoesNotThrow(() -> dealService.setApplicationStatus(application.getId(), ApplicationStatus.DOCUMENT_SIGNED, ChangeType.MANUAL));
    }

    @Test
    void getLoanOffers() {
        Client client = Instancio.create(Client.class);
        client.setId(1L);
        LoanApplicationRequestDTO request = Instancio.create(LoanApplicationRequestDTO.class);

        Mockito.when(clientService.findClientByPassportSeriesAndPassportNumber(request.getPassportSeries(),request.getPassportNumber())).thenReturn(client);
        Mockito.when(applicationService.checkIfAppliedOfferExists(client.getApplication())).thenReturn(true);

        assertThrows(IncorrectApplicationStatusException.class, () -> dealService.createLoanOffera(request));

        Mockito.when(applicationService.checkIfAppliedOfferExists(client.getApplication())).thenReturn(false);
        Mockito.when(clientService.getClientIdByEmail(request.getEmail())).thenReturn(2L);
        assertThrows(UniqueConstraintViolationException.class, () -> dealService.createLoanOffera(request));

        Mockito.when(clientService.findClientByPassportSeriesAndPassportNumber(request.getPassportSeries(),request.getPassportNumber())).thenReturn(null);
        Mockito.when(clientService.getClientIdByEmail(request.getEmail())).thenReturn(1L);
        Mockito.when(clientService.existsClientByEmail(request.getEmail())).thenReturn(true);
        assertThrows(UniqueConstraintViolationException.class, () -> dealService.createLoanOffera(request));
    }


}