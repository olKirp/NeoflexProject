package neostudy.application.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import neostudy.application.dto.LoanApplicationRequestDTO;
import neostudy.application.dto.LoanOfferDTO;
import neostudy.application.service.ApplicationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationServiceImpl;

    @PostMapping("/application")
    @Operation(summary= "Calculation of possible loan offers", description = "Returns four LoanOfferDTO or message with the reason for rejection of the application")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "CreditDTO successfully created or the application was rejected, but LoanApplicationRequestDTO was valid"),
            @ApiResponse(responseCode = "400", description = "LoanApplicationRequestDTO is not valid"),
            @ApiResponse(responseCode = "500", description = "Deal server unavailable or internal server error occured"),
            @ApiResponse(responseCode = "409", description = "Client has approved application and application cannot be changed")})
    public ResponseEntity<List<LoanOfferDTO>> createLoanOffers(@Valid @RequestBody LoanApplicationRequestDTO loanRequest) {
        return applicationServiceImpl.createLoanOffers(loanRequest);
    }

    @PostMapping("/application/offer")
    @ResponseStatus(value = HttpStatus.OK)
    @Operation(summary= "Select loan offer", description = "Receives offer selected by user and save it to database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Loan offer was successfully saved"),
            @ApiResponse(responseCode = "400", description = "LoanOfferDTO is not valid"),
            @ApiResponse(responseCode = "404", description = "Application for LoanOfferDTO not found"),
            @ApiResponse(responseCode = "409", description = "The application has already been approved and cannot be changed")})
    public void saveLoanOffer(@Valid @RequestBody LoanOfferDTO appliedOffer) {
        applicationServiceImpl.applyLoanOffer(appliedOffer);
    }
}
