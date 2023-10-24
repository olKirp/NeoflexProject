package neostudy.deal.service;

import neostudy.deal.dto.LoanOfferDTO;
import neostudy.deal.dto.ApplicationStatus;
import neostudy.deal.dto.ChangeType;
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

    private static final ApplicationRepository applicationRepository = Mockito.mock();

    @BeforeAll
    static void init() {
        applicationWithNullOffer.setAppliedOffer(null);
        applicationWithNullOffer.setId(2L);

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
        Mockito.when(applicationRepository.findById(3L)).thenReturn(Optional.of(application));
        assertEquals(Optional.of(application), applicationService.findApplicationById(3L));
    }

    @Test
    void getApplicationForClientIfAppNotExists() {
        Client client = Instancio.create(Client.class);
        Application application = applicationService.getApplicationForClient(client);

        assertEquals(client, application.getClient());
        assertEquals(ApplicationStatus.PREAPPROVAL, application.getStatus());
        assertNotNull(application.getCreationDate());
    }

    @Test
    void getApplicationForClientIfAppExists() {
        Application expectedApp = Instancio.create(Application.class);

        Mockito.when(applicationRepository.findApplicationByClientId(expectedApp.getClient().getId())).thenReturn(Optional.ofNullable(expectedApp));

        Application application = applicationService.getApplicationForClient(expectedApp.getClient());

        assertEquals(expectedApp.getClient(), application.getClient());
        assertEquals(expectedApp.getStatus(), application.getStatus());
        assertEquals(expectedApp.getSesCode(), application.getSesCode());
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
    void invokeMethodsWithNull() {
        assertThrows(IllegalArgumentException.class, () -> applicationService.setApplicationStatus(null, null, null));
        assertThrows(IllegalArgumentException.class, () -> applicationService.saveApplication(null));
        assertThrows(IllegalArgumentException.class, () -> applicationService.setLoanOfferToApplication(null, null));
        assertThrows(IllegalArgumentException.class, () -> applicationService.getApplicationForClient (null));
        assertThrows(IllegalArgumentException.class, () -> applicationService.isApplicationApprovedByConveyor(null));
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

        application.setStatus(ApplicationStatus.DOCUMENTS_CREATED);
        assertTrue(applicationService.isApplicationApprovedByConveyor(application));

        application.setStatus(ApplicationStatus.DOCUMENT_SIGNED);
        assertTrue(applicationService.isApplicationApprovedByConveyor(application));
    }
}