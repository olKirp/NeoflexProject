package neostudy.deal.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import neostudy.deal.dto.FinishRegistrationRequestDTO;
import neostudy.deal.dto.LoanApplicationRequestDTO;
import neostudy.deal.dto.LoanOfferDTO;
import neostudy.deal.service.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class DealController {

    private final DealService dealService;

    @PostMapping("/deal/application")
    @Operation(summary = "Calculation of possible loan offers", description = "Returns four LoanOfferDTO or message with the reason for rejection of the application")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "CreditDTO successfully created or the application was rejected, but LoanApplicationRequestDTO was valid"),
            @ApiResponse(responseCode = "400", description = "LoanApplicationRequestDTO is not valid"),
            @ApiResponse(responseCode = "500", description = "Conveyor server unavailable or internal server error occured"),
            @ApiResponse(responseCode = "409", description = "Client has approved application and application cannot be changed")})
    public ResponseEntity<List<LoanOfferDTO>> createLoanOffers(@Valid @RequestBody LoanApplicationRequestDTO loanRequest) {
        List<LoanOfferDTO> offers = dealService.getLoanOffers(loanRequest);
        return ResponseEntity.ok(offers);
    }

    @PostMapping("/deal/offer")
    @ResponseStatus(value = HttpStatus.OK)
    @Operation(summary = "Select loan offer", description = "Receives offer selected by user and save it to database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Loan offer was successfully saved"),
            @ApiResponse(responseCode = "400", description = "LoanOfferDTO is not valid"),
            @ApiResponse(responseCode = "404", description = "Application for LoanOfferDTO not found"),
            @ApiResponse(responseCode = "409", description = "The application has already been approved and cannot be changed")})
    public void saveLoanOffer(@Valid @RequestBody LoanOfferDTO appliedOffer) {
        dealService.approveLoanOffer(appliedOffer);
    }

    @PostMapping("/deal/calculate/{applicationId}")
    @ResponseStatus(value = HttpStatus.OK)
    @Operation(summary = "Create CreditDTO",
            description = "Receives finish registration request and ID of application which was created earlier, than creates CreditDTO and saves it to database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "CreditDTO successfully created or the application was rejected, but user's data was valid"),
            @ApiResponse(responseCode = "400", description = "FinishRegistrationRequestDTO is not valid"),
            @ApiResponse(responseCode = "404", description = "Application not found"),
            @ApiResponse(responseCode = "409", description = "The application has already been approved and cannot be changed"),
            @ApiResponse(responseCode = "500", description = "Conveyor server unavailable or internal server error occured")})
    public void createCredit(@Valid @RequestBody FinishRegistrationRequestDTO registrationRequest,
                             @Parameter(name = "applicationId",
                                     description = "ID of application",
                                     example = "1",
                                     required = true)
                             @PathVariable("applicationId") Long applicationId) {
        dealService.createCreditForApplication(registrationRequest, applicationId);
    }
}
