package neostudy.deal.controller;

import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import neostudy.deal.service.DocumentsService;
import org.openapitools.api.DocumentsControllerApi;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@RequiredArgsConstructor
@Controller("/deal/document")
@Validated
public class DocumentsController implements DocumentsControllerApi {

    private final DocumentsService documentsServiceImpl;

    @Override
    public ResponseEntity<String> sendDocuments(@PathVariable("applicationId") Long applicationId) {
        documentsServiceImpl.sendDocuments(applicationId);
        return ResponseEntity.ok("Documents have been sent");
    }


    @Override
    public ResponseEntity<String> signDocumentsRequest(@PathVariable("applicationId") Long applicationId) {
        documentsServiceImpl.signDocumentsRequest(applicationId);
        return ResponseEntity.ok("SES-code has been sent");
    }

    @Override
    public ResponseEntity<String> signDocuments(@PathVariable("applicationId") Long applicationId, @RequestBody String sesCode) {
        documentsServiceImpl.signDocuments(applicationId, sesCode);
        return ResponseEntity.ok("Documents have been signed");
    }
}
