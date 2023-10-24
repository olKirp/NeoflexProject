package neostudy.deal.service;

import neostudy.deal.dto.ApplicationStatus;
import neostudy.deal.entity.Application;
import neostudy.deal.exceptions.IncorrectApplicationStatusException;
import neostudy.deal.exceptions.IncorrectSesCodeException;
import neostudy.deal.exceptions.NotFoundException;
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
class DocumentsServiceImplImplTest {

    private static DocumentsServiceImpl documentsServiceImpl;

    private static Application application = Instancio.create(Application.class);

    @Mock
    private static KafkaMessageSenderService msgSender;

    @Mock
    private static ApplicationService applicationService;

    @BeforeAll
    static void init() {
        applicationService = Mockito.mock();
        msgSender = Mockito.mock();
        documentsServiceImpl = new DocumentsServiceImpl(msgSender, applicationService);
        Mockito.when(applicationService.findApplicationById(application.getId())).thenReturn(Optional.ofNullable(application));
        Mockito.when(applicationService.findApplicationById(0L)).thenThrow(new NotFoundException("Application 0 not found"));
    }

    @Test
    void sendDocuments() {
        for (ApplicationStatus status : ApplicationStatus.values()) {
            application.setStatus(status);
            String expected = "Application with id " + application.getId() + " must have status " + ApplicationStatus.CC_APPROVED + ", but has status " + status;
            if (status != ApplicationStatus.CC_APPROVED) {
                Exception exception = assertThrows(IncorrectApplicationStatusException.class, () -> documentsServiceImpl.sendDocuments(application.getId()));
                assertEquals(expected, exception.getMessage());
            } else {
                assertDoesNotThrow(() -> documentsServiceImpl.sendDocuments(application.getId()));
            }
        }
    }

    @Test
    void signDocumentsRequest() {
        for (ApplicationStatus status : ApplicationStatus.values()) {
            application.setStatus(status);
            String expected = "Application with id " + application.getId() + " must have status " + ApplicationStatus.DOCUMENTS_CREATED + ", but has status " + status;
            if (status != ApplicationStatus.DOCUMENTS_CREATED) {
                Exception exception = assertThrows(IncorrectApplicationStatusException.class, () -> documentsServiceImpl.signDocumentsRequest(application.getId()));
                assertEquals(expected, exception.getMessage());
            } else {
                assertDoesNotThrow(() -> documentsServiceImpl.signDocumentsRequest(application.getId()));
            }
        }
    }

    @Test
    void signDocuments() {
        for (ApplicationStatus status : ApplicationStatus.values()) {
            application.setStatus(status);
            String expected = "Application with id " + application.getId() + " must have status " + ApplicationStatus.DOCUMENTS_CREATED + ", but has status " + status;
            if (status != ApplicationStatus.DOCUMENTS_CREATED) {
                Exception exception = assertThrows(IncorrectApplicationStatusException.class, () -> documentsServiceImpl.signDocuments(application.getId(), application.getSesCode()));
                assertEquals(expected, exception.getMessage());
            } else {
                assertDoesNotThrow(() -> documentsServiceImpl.signDocuments(application.getId(), application.getSesCode()));
            }
        }
    }

    @Test
    void sesCodeTest() {
        application.setStatus(ApplicationStatus.DOCUMENTS_CREATED);
        application.setSesCode("2222");
        String expected = "The SES-code doesn't match application's " + application.getId() + " SES-code";
        Exception exception = assertThrows(IncorrectSesCodeException.class, () -> documentsServiceImpl.signDocuments(application.getId(), "1111"));
        assertEquals(expected, exception.getMessage());
    }

    @Test
    void notFoundTest() {
        String expected = "Application 0 not found";
        Exception exception = assertThrows(NotFoundException.class, () -> documentsServiceImpl.sendDocuments(0L));
        assertEquals(expected, exception.getMessage());

        exception = assertThrows(NotFoundException.class, () -> documentsServiceImpl.signDocumentsRequest(0L));
        assertEquals(expected, exception.getMessage());

        exception = assertThrows(NotFoundException.class, () -> documentsServiceImpl.signDocuments(0L, "1111"));
        assertEquals(expected, exception.getMessage());
    }
}