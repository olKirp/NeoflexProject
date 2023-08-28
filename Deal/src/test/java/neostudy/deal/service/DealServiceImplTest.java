package neostudy.deal.service;

import neostudy.deal.dto.FinishRegistrationRequestDTO;
import neostudy.deal.dto.LoanOfferDTO;
import neostudy.deal.entity.Application;
import neostudy.deal.exceptions.ApplicationAlreadyApprovedException;
import neostudy.deal.exceptions.NotFoundException;
import neostudy.deal.feignclient.ConveyorAPIClient;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class DealServiceImplTest {

    private static DealServiceImpl dealService;
    
    private static final Application application = Instancio.create(Application.class);
    
    private static FinishRegistrationService finishRegistrationService;
    private static ApplicationService applicationService;
    private static  ClientService clientService;
    private static CreditService creditService;
    private static ConveyorAPIClient conveyorAPIClient;

    @BeforeAll
    static void init() {
        finishRegistrationService = Mockito.mock();
        applicationService = Mockito.mock();
        clientService = Mockito.mock();
        creditService = Mockito.mock();
        conveyorAPIClient = Mockito.mock();
        dealService = new DealServiceImpl(finishRegistrationService, applicationService, clientService, creditService, conveyorAPIClient);

        Mockito.when(applicationService.getApplicationById(application.getId())).thenReturn(application);

    }

    @Test
    void approveLoanOffer() {
        Mockito.when(applicationService.isApplicationExists(application.getId())).thenReturn(false);

        LoanOfferDTO loanOffer = LoanOfferDTO.builder()
                .applicationId(application.getId())
                .build();

        Exception exception = assertThrows(NotFoundException.class, () -> dealService.approveLoanOffer(loanOffer));
        assertEquals("Application " + application.getId() + " not found", exception.getMessage());

        Mockito.when(applicationService.isApplicationExists(application.getId())).thenReturn(true);

        Mockito.when(applicationService.isApplicationApprovedByConveyor(application)).thenReturn(true);
        exception = assertThrows(ApplicationAlreadyApprovedException.class, () -> dealService.approveLoanOffer(loanOffer));
        assertEquals("Application " + application.getId() + " has status " + application.getStatus() + " and cannot be changed", exception.getMessage());
    }

    @Test
    void createCreditForApplication() {
        Mockito.when(applicationService.checkIfAppliedOfferExists(application)).thenReturn(false);

        Exception exception = assertThrows(NotFoundException.class, () -> dealService.createCreditForApplication(new FinishRegistrationRequestDTO(), application.getId()));
        assertEquals("No applied offers for application " + application.getId(), exception.getMessage());

        Mockito.when(applicationService.checkIfAppliedOfferExists(application)).thenReturn(true);
        Mockito.when(applicationService.isApplicationApprovedByConveyor(application)).thenReturn(true);
        exception = assertThrows(ApplicationAlreadyApprovedException.class, () -> dealService.createCreditForApplication(new FinishRegistrationRequestDTO(), application.getId()));
        assertEquals("Application " + application.getId() + " has status " + application.getStatus() + " and cannot be changed", exception.getMessage());
    }
}