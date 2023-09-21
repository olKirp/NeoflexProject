package neostudy.deal.service;

import feign.FeignException;
import feign.Request;
import neostudy.deal.dto.*;
import neostudy.deal.entity.Application;
import neostudy.deal.entity.Client;
import neostudy.deal.exceptions.CreditConveyorDeniedException;
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
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DealServiceImplTest {
    private static DealServiceImpl dealService;
    private static final Application application = Instancio.create(Application.class);
    private static final LoanApplicationRequestDTO request = Instancio.create(LoanApplicationRequestDTO.class);

    private static FinishRegistrationService finishRegistrationService;
    private static ApplicationService applicationService;
    private static ClientService clientService;
    private static CreditService creditService;
    private static ConveyorAPIClient conveyorAPIClient;
    private static KafkaMessageSenderService msgSender;

    @BeforeAll
    static void init() {
        application.setId(1L);
        finishRegistrationService = Mockito.mock();
        applicationService = Mockito.mock();
        clientService = Mockito.mock();
        creditService = Mockito.mock();
        conveyorAPIClient = Mockito.mock();
        msgSender = Mockito.mock();

        dealService = new DealServiceImpl(finishRegistrationService, applicationService, clientService, creditService, conveyorAPIClient, msgSender);

        Mockito.when(applicationService.findApplicationById(application.getId())).thenReturn(Optional.of(application));
    }

    @Test
    void getLoanOffers() {
        LoanApplicationRequestDTO requestDTO = Instancio.create(LoanApplicationRequestDTO.class);

        Client client = Instancio.create(Client.class);
        client.setId(3L);
        client.getApplication().setAppliedOffer(null);

        Application app = Instancio.create(Application.class);

        Mockito.when(clientService.createClientForLoanRequest(requestDTO)).thenReturn(client);
        Mockito.when(applicationService.getApplicationForClient(client)).thenReturn(app);
        Mockito.when(applicationService.saveApplication(app)).thenReturn(app);

        List<LoanOfferDTO> arrayList = new ArrayList<>();
        arrayList.add(new LoanOfferDTO());
        Mockito.when(conveyorAPIClient.createLoanOffers(requestDTO)).thenReturn(ResponseEntity.ok(arrayList));

        List<LoanOfferDTO> result = dealService.createLoanOffers(requestDTO);
        assertEquals(result.get(0).getApplicationId(), app.getId());
        System.out.println(result);
    }
    @Test
    void getLoanOffersWhenOpenApiExceptions() {
        LoanApplicationRequestDTO requestDTO = Instancio.create(LoanApplicationRequestDTO.class);

        Client client = Instancio.create(Client.class);
        client.setId(3L);
        client.getApplication().setAppliedOffer(null);

        Application app = Instancio.create(Application.class);

        Mockito.when(clientService.createClientForLoanRequest(requestDTO)).thenReturn(client);
        Mockito.when(applicationService.getApplicationForClient(client)).thenReturn(app);
        Mockito.when(conveyorAPIClient.createLoanOffers(requestDTO)).thenThrow(new FeignException.BadRequest("", Instancio.create(Request.class), null, null));
        assertThrows(CreditConveyorDeniedException.class, () -> dealService.createLoanOffers(requestDTO));

        LoanApplicationRequestDTO requestDTO2 = Instancio.create(LoanApplicationRequestDTO.class);
        Mockito.when(conveyorAPIClient.createLoanOffers(requestDTO2)).thenThrow(new FeignException.InternalServerError("", Instancio.create(Request.class), null, null));
        assertThrows(FeignException.InternalServerError.class, () -> dealService.createLoanOffers(requestDTO2));
    }

    @Test
    void approveLoanOfferWhenApplicationHasIncorrectStatus() {
        LoanOfferDTO loanOffer = new LoanOfferDTO();
        loanOffer.setApplicationId(application.getId());
        Mockito.when(applicationService.isApplicationApprovedByConveyor(application)).thenReturn(true);
        Exception exception = assertThrows(IncorrectApplicationStatusException.class, () -> dealService.approveLoanOffer(loanOffer));
        assertEquals("Application " + application.getId() + " has status " + application.getStatus() + " and cannot be changed", exception.getMessage());
    }

    @Test
    void sendMsg() {
        assertDoesNotThrow(() -> dealService.sendMessage(1L, Theme.SEND_SES));
    }

    @Test
    void approveLoanOfferWhenApplicationDoesNotExists() {
        Mockito.when(applicationService.findApplicationById(10L)).thenReturn(Optional.ofNullable(null));

        LoanOfferDTO loanOffer = new LoanOfferDTO();
        loanOffer.setApplicationId(10L);

        Exception exception = assertThrows(NotFoundException.class, () -> dealService.approveLoanOffer(loanOffer));
        assertEquals("Application 10 not found", exception.getMessage());
    }
    @Test
    void createCreditForApplicationWhenNoOffers() {
        Application app = Instancio.create(Application.class);
        app.setAppliedOffer(null);
        app.setId(11L);
        Mockito.when(applicationService.findApplicationById(11L)).thenReturn(Optional.of(app));

        Exception exception = assertThrows(NotFoundException.class, () -> dealService.createCreditForApplication(new FinishRegistrationRequestDTO(), 11L));
        assertEquals("No applied offers for application 11", exception.getMessage());
    }

    @Test
    void createCreditForApplicationWhenIncorrectStatus() {
        Application app = Instancio.create(Application.class);
        app.setStatus(ApplicationStatus.CREDIT_ISSUED);

        app.setId(12L);
        Mockito.when(applicationService.findApplicationById(12L)).thenReturn(Optional.of(app));

        Mockito.when(applicationService.isApplicationApprovedByConveyor(app)).thenReturn(true);
        Exception exception = assertThrows(IncorrectApplicationStatusException.class, () -> dealService.createCreditForApplication(new FinishRegistrationRequestDTO(), 12L));
        assertEquals("Application 12 has status CREDIT_ISSUED and cannot be changed", exception.getMessage());
    }

    @Test
    void setApplicationStatus() {
        application.setStatus(ApplicationStatus.PREAPPROVAL);
        assertDoesNotThrow(() -> dealService.setAndSaveApplicationStatus(application.getId(), ApplicationStatus.DOCUMENT_SIGNED, ChangeType.MANUAL));
    }

    @Test
    void invokeMethodsWithNull() {
        assertThrows(IllegalArgumentException.class, () -> dealService.sendMessage(1L, null));
        assertThrows(IllegalArgumentException.class, () -> dealService.createLoanOffers(null));
        assertThrows(IllegalArgumentException.class, () -> dealService.setAndSaveApplicationStatus(1L, null, null));
        assertThrows(IllegalArgumentException.class, () -> dealService.createCreditForApplication(null, 1L));
        assertThrows(IllegalArgumentException.class, () -> dealService.approveLoanOffer(null));
    }

    @Test
    void getLoanOffersWhenIncorrectStatus() {
        Client client = Instancio.create(Client.class);
        client.setId(1L);


        Mockito.when(clientService.findClientByPassportSeriesAndPassportNumber(request.getPassportSeries(),request.getPassportNumber())).thenReturn(Optional.of(client));
        assertThrows(IncorrectApplicationStatusException.class, () -> dealService.createLoanOffers(request));
    }

    @Test
    void getLoanOffersWhenAnotherClientHasSameEmail() {
        Client client = Instancio.create(Client.class);
        client.setId(2L);
        client.getApplication().setAppliedOffer(null);
        request.setEmail("email@mail.com");
        client.setEmail("email2@mail.com");
        Mockito.when(clientService.findClientByPassportSeriesAndPassportNumber(client.getPassport().getSeries(), client.getPassport().getNumber())).thenReturn(Optional.of(client));
        Mockito.when(clientService.existsClientByEmail(request.getEmail())).thenReturn(true);
        assertThrows(UniqueConstraintViolationException.class, () -> dealService.createLoanOffers(request));

        Mockito.when(clientService.findClientByPassportSeriesAndPassportNumber(request.getPassportSeries(),request.getPassportNumber())).thenReturn(Optional.ofNullable(null));
        Mockito.when(clientService.existsClientByEmail(request.getEmail())).thenReturn(true);
        assertThrows(UniqueConstraintViolationException.class, () -> dealService.createLoanOffers(request));
    }

    @Test
    void getLoanOffersWhenClientDoesNotExistsAndAnotherClientHasSameEmail() {
        Client client = Instancio.create(Client.class);
        client.setId(2L);
        client.getApplication().setAppliedOffer(null);

        Mockito.when(clientService.findClientByPassportSeriesAndPassportNumber(request.getPassportSeries(),request.getPassportNumber())).thenReturn(Optional.ofNullable(null));
        Mockito.when(clientService.existsClientByEmail(request.getEmail())).thenReturn(true);
        assertThrows(UniqueConstraintViolationException.class, () -> dealService.createLoanOffers(request));
    }


}