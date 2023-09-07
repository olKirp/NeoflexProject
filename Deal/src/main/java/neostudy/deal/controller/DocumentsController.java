package neostudy.deal.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import neostudy.deal.service.DocumentsService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@RequiredArgsConstructor
@Controller("/deal/document")
@Validated
public class DocumentsController {

    private final DocumentsService documentsServiceImpl;

    @PostMapping("/{applicationId}/send")
    @ResponseStatus(value = HttpStatus.OK)
    @Operation(summary = "Request to send documents", description = "Receives user's request and sends document creation request to Dossier service")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Application is correct"),
            @ApiResponse(responseCode = "404", description = "Application not found"),
            @ApiResponse(responseCode = "409", description = "Application has incorrect status")})
    public void sendDocuments(@PathVariable("applicationId") Long applicationId) {
        documentsServiceImpl.sendDocuments(applicationId);
    }

    @PostMapping("/{applicationId}/sign")
    @ResponseStatus(value = HttpStatus.OK)
    @Operation(summary = "Request for ses-code", description = "Sends ses-code for documents signing")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Application is correct"),
            @ApiResponse(responseCode = "404", description = "Application not found"),
            @ApiResponse(responseCode = "409", description = "Application has incorrect status")})
    public void signDocumentsRequest(@PathVariable("applicationId") Long applicationId) {
        documentsServiceImpl.signDocumentsRequest(applicationId);
    }

    @PostMapping("/{applicationId}/code")
    @ResponseStatus(value = HttpStatus.OK)
    @Operation(summary = "Request to sign documents", description = "Signs documents")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ses-code was correct and documents successfully signed"),
            @ApiResponse(responseCode = "400", description = "Ses-code does not match regexp"),
            @ApiResponse(responseCode = "404", description = "Application not found"),
            @ApiResponse(responseCode = "409", description = "Incorrect ses-code or application has incorrect status")
    })
    public void signDocuments(@PathVariable("applicationId") Long applicationId, @RequestBody @Pattern(regexp = "[0-9]{4}") String sesCode) {
        documentsServiceImpl.signDocuments(applicationId, sesCode);
    }
}
