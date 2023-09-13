package neostudy.gateway.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import neostudy.gateway.dto.FinishRegistrationRequestDTO;
import neostudy.gateway.feignclient.DealFeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Controller
@Validated
@RequiredArgsConstructor
public class GatewayDealController {

    private final DealFeignClient dealFeignClient;

    @PostMapping("/gateway/document/{applicationId}/send")
    @ResponseStatus(value = HttpStatus.OK)
    @Operation(summary = "Request to send documents", description = "Receives user's request and sends document creation request to Dossier service")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Application is correct"),
            @ApiResponse(responseCode = "404", description = "Application not found"),
            @ApiResponse(responseCode = "409", description = "Application has incorrect status")})
    public void sendDocuments(@PathVariable("applicationId") Long applicationId) {
        dealFeignClient.sendDocuments(applicationId);
    }

    @PostMapping("/gateway/document/{applicationId}/sign")
    @ResponseStatus(value = HttpStatus.OK)
    @Operation(summary = "Request for ses-code", description = "Sends ses-code for documents signing")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Application is correct"),
            @ApiResponse(responseCode = "404", description = "Application not found"),
            @ApiResponse(responseCode = "409", description = "Application has incorrect status")})
    public void signDocumentsRequest(@PathVariable("applicationId") Long applicationId) {
        dealFeignClient.signDocumentsRequest(applicationId);
    }

    @PostMapping("/gateway/document{applicationId}/code")
    @ResponseStatus(value = HttpStatus.OK)
    @Operation(summary = "Request to sign documents", description = "Signs documents")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ses-code was correct and documents successfully signed"),
            @ApiResponse(responseCode = "400", description = "Ses-code does not match regexp"),
            @ApiResponse(responseCode = "404", description = "Application not found"),
            @ApiResponse(responseCode = "409", description = "Incorrect ses-code or application has incorrect status")
    })
    public void signDocuments(@PathVariable("applicationId") Long applicationId, @RequestBody @Pattern(regexp = "[0-9]{4}") String sesCode) {
        dealFeignClient.signDocuments(applicationId, sesCode);
    }

    @PutMapping("/gateway/deal/calculate/{applicationId}")
    @ResponseStatus(value = HttpStatus.OK)
    @Operation(summary = "Create CreditDTO",
            description = "Receives finish registration request and ID of application which was created earlier, than creates CreditDTO and saves it to database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "CreditDTO successfully created or the application was rejected, but user's data was valid"),
            @ApiResponse(responseCode = "400", description = "FinishRegistrationRequestDTO is not valid"),
            @ApiResponse(responseCode = "404", description = "Application not found"),
            @ApiResponse(responseCode = "409", description = "Application cannot be changed or client with same INN or account number already exists"),
            @ApiResponse(responseCode = "500", description = "Conveyor server unavailable or internal server error occured")})
    public void createCredit(@Valid @RequestBody FinishRegistrationRequestDTO registrationRequest,
                             @Parameter(name = "applicationId",
                                     description = "ID of application",
                                     example = "1",
                                     required = true)
                             @PathVariable("applicationId") Long applicationId) {
        dealFeignClient.createCredit(registrationRequest, applicationId);
    }


}
