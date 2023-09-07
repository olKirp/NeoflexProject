package neostudy.dossier.services;

import neostudy.dossier.dto.ApplicationDTO;
import neostudy.dossier.dto.enums.Theme;
import neostudy.dossier.feignclient.DealAPIClient;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class MailCreatorServiceImplTest {

    static MailCreatorServiceImpl mailBodyService;

    static ApplicationDTO applicationDTO;

    static String signDocuments = "signDocuments";

    static String sendDocuments = "sendDocuments";

    static String signDocumentsRequest = "signDocumentsRequest";

    static String finishRegistration = "finishRegistration";

    static DealAPIClient apiClient;

    @BeforeAll
    static void init() {
        apiClient = Mockito.mock(DealAPIClient.class);
        applicationDTO = Instancio.create(ApplicationDTO.class);
        Mockito.when(apiClient.getApplicationById(applicationDTO.getId())).thenReturn(ResponseEntity.of(Optional.of(applicationDTO)));

        mailBodyService = new MailCreatorServiceImpl(apiClient);

        mailBodyService.setFinishRegistration(finishRegistration);
        mailBodyService.setSendDocuments(sendDocuments);
        mailBodyService.setSignDocumentsRequest(signDocumentsRequest);
        mailBodyService.setSignDocuments(signDocuments);
    }

    @Test
    void createMailBody() {
        String id = String.valueOf(applicationDTO.getId());
        for (Theme theme : Theme.values()) {

            String link = "";
            switch (theme) {
                case SEND_SES -> link = signDocuments;
                case SEND_DOCUMENTS -> link = signDocumentsRequest;
                case FINISH_REGISTRATION -> link = finishRegistration;
                case CREATE_DOCUMENTS -> link = sendDocuments;
            }
            String body = mailBodyService.createMailBody(theme, applicationDTO.getId());
            assertTrue(body.contains(id));
            assertTrue(body.endsWith(link));
        }
    }

}