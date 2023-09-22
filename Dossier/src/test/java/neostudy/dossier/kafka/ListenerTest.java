package neostudy.dossier.kafka;

import feign.FeignException;
import jakarta.mail.MessagingException;
import neostudy.dossier.dto.ApplicationDTO;
import neostudy.dossier.dto.EmailMessageDTO;
import neostudy.dossier.exceptions.DealMicroserviceException;
import neostudy.dossier.feignclient.DealAPIClient;
import neostudy.dossier.services.DocumentsGeneratorService;
import neostudy.dossier.services.MailCreatorService;
import neostudy.dossier.services.MailSenderService;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailSendException;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(OutputCaptureExtension.class)
class ListenerTest {
    static Listener listener;

    static MailCreatorService mailCreatorService;
    static DealAPIClient apiClient;
    static DocumentsGeneratorService documentsGenerator;

    static MailSenderService mailSender;

    @BeforeAll
    static void init() {
        mailCreatorService = Mockito.mock(MailCreatorService.class);

        mailSender = Mockito.mock(MailSenderService.class);

        apiClient = Mockito.mock(DealAPIClient.class);

        documentsGenerator = Mockito.mock(DocumentsGeneratorService.class);

        listener = new Listener(mailCreatorService, mailSender, apiClient, documentsGenerator);
    }

    @Test
    void handleFinishRegistration() {
        assertDoesNotThrow(() -> listener.handleFinishRegistration(new EmailMessageDTO()));
    }

    @Test
    void handleCreateDocuments() {
        assertDoesNotThrow(() -> listener.handleCreateDocuments(new EmailMessageDTO()));
    }

    @Test
    void handleSendDocuments(CapturedOutput output) throws MessagingException, IOException {
        EmailMessageDTO messageDTO = new EmailMessageDTO();
        ApplicationDTO app = Instancio.create(ApplicationDTO.class);
        messageDTO.setApplicationId(1L);

        Mockito.when(apiClient.getApplicationById(1L)).thenReturn(ResponseEntity.ok(app));
        assertDoesNotThrow(() -> listener.handleSendDocuments(messageDTO));
    }

    @Test
    void handleSendDocumentsWhenIOException(CapturedOutput output) throws MessagingException, IOException {
        EmailMessageDTO messageDTO = new EmailMessageDTO();
        ApplicationDTO app = Instancio.create(ApplicationDTO.class);
        messageDTO.setApplicationId(1L);

        Mockito.when(apiClient.getApplicationById(1L)).thenReturn(ResponseEntity.ok(app));
        Mockito.when(documentsGenerator.generateAllDocuments(app)).thenThrow(IOException.class);
        listener.handleSendDocuments(messageDTO);
        assertTrue(output.getOut().contains("Exception during documents generation"));
    }

    @Test
    void handleSendDocumentsWhenAppIsNull(CapturedOutput output) throws MessagingException, IOException {
        EmailMessageDTO messageDTO = new EmailMessageDTO();
        ApplicationDTO app = null;
        messageDTO.setApplicationId(2L);

        Mockito.when(apiClient.getApplicationById(2L)).thenReturn(ResponseEntity.ok(app));
        Mockito.when(documentsGenerator.generateAllDocuments(app)).thenThrow(IllegalArgumentException.class);
        listener.handleSendDocuments(messageDTO);
        assertTrue(output.getOut().contains("Illegal argument exception"));
    }

    @Test
    void handleSendDocumentsWhenMsgSenderException(CapturedOutput output) throws MessagingException, IOException {
        EmailMessageDTO messageDTO = new EmailMessageDTO();
        ApplicationDTO app = Instancio.create(ApplicationDTO.class);
        messageDTO.setApplicationId(3L);

        Mockito.when(apiClient.getApplicationById(3L)).thenThrow(FeignException.class);
        listener.handleSendDocuments(messageDTO);
        assertTrue(output.getOut().contains("Feign exception"));
    }

    @Test
    void handleSendDocumentsWhenDealException(CapturedOutput output) throws MessagingException, IOException {
        EmailMessageDTO messageDTO = new EmailMessageDTO();
        ApplicationDTO app = Instancio.create(ApplicationDTO.class);
        messageDTO.setApplicationId(4L);

        Mockito.when(apiClient.getApplicationById(4L)).thenThrow(DealMicroserviceException.class);
        listener.handleSendDocuments(messageDTO);
        assertTrue(output.getOut().contains("Deal microservice exception"));
    }

    @Test
    void handleSendSes() {
        assertDoesNotThrow(() -> listener.handleSendSes(new EmailMessageDTO()));
    }

    @Test
    void handleCreditIssued() {
        assertDoesNotThrow(() -> listener.handleCreditIssued(new EmailMessageDTO()));
    }

    @Test
    void handleApplicationDenied() {
        assertDoesNotThrow(() -> listener.handleApplicationDenied(new EmailMessageDTO()));
    }
}