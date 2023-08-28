package neostudy.deal.service;

import neostudy.deal.dto.LoanOfferDTO;
import neostudy.deal.dto.enums.ApplicationStatus;
import neostudy.deal.dto.enums.ChangeType;
import neostudy.deal.entity.Application;
import neostudy.deal.entity.Client;
import neostudy.deal.exceptions.NotFoundException;
import neostudy.deal.repository.ApplicationRepository;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ApplicationServiceImplTest {

    private static ApplicationServiceImpl applicationService;

    private static final Application application = Instancio.create(Application.class);
    private static final Application applicationWithNullOffer = Instancio.create(Application.class);

    @BeforeAll
    static void init(@Mock ApplicationRepository applicationRepository) {
        applicationWithNullOffer.setAppliedOffer(null);
        applicationWithNullOffer.setId(2L);

        Mockito.when(applicationRepository.existsApplicationByClientId(1L)).thenReturn(false);
        Mockito.when(applicationRepository.findById(1L)).thenReturn(Optional.empty());
        Mockito.when(applicationRepository.findById(2L)).thenReturn(Optional.of(applicationWithNullOffer));
        Mockito.when(applicationRepository.findById(3L)).thenReturn(Optional.of(application));

        applicationService = new ApplicationServiceImpl(applicationRepository);
    }

    @Test
    void setApplicationStatus() {
        application.setStatus(ApplicationStatus.PREAPPROVAL);
        application.setStatusHistory(null);

        applicationService.setApplicationStatus(application, ApplicationStatus.CC_APPROVED, ChangeType.AUTOMATIC);

        assertEquals(ApplicationStatus.CC_APPROVED, application.getStatus());
        assertEquals(ChangeType.AUTOMATIC, application.getStatusHistory().getChangeType());
        assertEquals(ApplicationStatus.CC_APPROVED, application.getStatusHistory().getStatus());
    }

    @Test
    void getApplicationById() {
        String correctMsg = "Application 1 not found";
        Exception exception = assertThrows(NotFoundException.class, () -> applicationService.getApplicationById(1L));
        assertEquals(correctMsg, exception.getMessage());

        assertEquals(application, applicationService.getApplicationById(3L));
    }

    @Test
    void createApplicationForClient() {
        Client client = Instancio.create(Client.class);
        Application application = applicationService.createApplicationForClient(client);

        assertEquals(client, application.getClient());
        assertEquals(ApplicationStatus.PREAPPROVAL, application.getStatus());
        assertNotNull(application.getCreationDate());
        assertNotNull(application.getSignDate());
    }

    @Test
    void setLoanOfferToApplication() {
        LoanOfferDTO loanOffer = Instancio.create(LoanOfferDTO.class);
        application.setStatus(ApplicationStatus.PREAPPROVAL);

        applicationService.setLoanOfferToApplication(application, loanOffer);
        assertEquals(loanOffer, application.getAppliedOffer());
        assertEquals(ApplicationStatus.APPROVED, application.getStatus());
    }

    @Test
    void isApplicationApprovedByConveyor() {
        application.setStatus(ApplicationStatus.PREAPPROVAL);
        assertFalse(applicationService.isApplicationApprovedByConveyor(application));

        application.setStatus(ApplicationStatus.APPROVED);
        assertFalse(applicationService.isApplicationApprovedByConveyor(application));

        application.setStatus(ApplicationStatus.CLIENT_DENIED);
        assertFalse(applicationService.isApplicationApprovedByConveyor(application));

        application.setStatus(ApplicationStatus.CC_DENIED);
        assertFalse(applicationService.isApplicationApprovedByConveyor(application));

        application.setStatus(ApplicationStatus.CC_APPROVED);
        assertTrue(applicationService.isApplicationApprovedByConveyor(application));

        application.setStatus(ApplicationStatus.CREDIT_ISSUED);
        assertTrue(applicationService.isApplicationApprovedByConveyor(application));

        application.setStatus(ApplicationStatus.DOCUMENT_CREATED);
        assertTrue(applicationService.isApplicationApprovedByConveyor(application));

        application.setStatus(ApplicationStatus.DOCUMENT_SIGNED);
        assertTrue(applicationService.isApplicationApprovedByConveyor(application));
    }
}