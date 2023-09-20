package neostudy.dossier.services;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import neostudy.dossier.dto.ApplicationDTO;
import neostudy.dossier.dto.Theme;
import neostudy.dossier.feignclient.DealAPIClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Data
@Service
@RequiredArgsConstructor
public class MailCreatorServiceImpl implements MailCreatorService {

    @Value("${links.signDocuments}")
    private String signDocuments;

    @Value("${links.sendDocuments}")
    private String sendDocuments;

    @Value("${links.signDocumentsRequest}")
    private String signDocumentsRequest;

    @Value("${links.finishRegistration}")
    private String finishRegistration;

    private final DealAPIClient apiClient;

    @Override
    public String createMailBody(Theme theme, Long appId) {
        String body = "";

        switch (theme) {
            case SEND_SES -> {
                ApplicationDTO application = apiClient.getApplicationById(appId).getBody();
                body = "The simple electronic signature code for application  " + appId + "  is " + application.getSesCode() + ". Follow the link and enter it: " + signDocuments;
            }
            case CREDIT_ISSUED -> body = "The loan for application " + appId + " was successfully issued! thank you for choosing our company";
            case CREATE_DOCUMENTS -> body = "Application " + appId + " was approved. Please follow the link below to get the documents: " + sendDocuments;
            case SEND_DOCUMENTS -> body = "Documents for application " + appId + " in the attachment. If the documents are correct, follow the link below to send a request to sign them: " + signDocumentsRequest;
            case APPLICATION_DENIED -> body = "Your loan application " + appId + " was denied";
            case FINISH_REGISTRATION -> body = "Your loan application " + appId + " was pre-approved. Please follow the link below to finish registration: " + finishRegistration;
        }

        return body;
    }
}
