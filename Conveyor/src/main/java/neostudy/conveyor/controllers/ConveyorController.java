package neostudy.conveyor.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import neostudy.conveyor.dto.CreditDTO;
import neostudy.conveyor.dto.LoanApplicationRequestDTO;
import neostudy.conveyor.dto.LoanOfferDTO;
import neostudy.conveyor.dto.ScoringDataDTO;
import neostudy.conveyor.service.PrescoringService;
import neostudy.conveyor.service.ScoringService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Log4j2
@RestController
@RequiredArgsConstructor
@Tag(name="Conveyor controller")
public class ConveyorController {

    private final PrescoringService prescoringService;

    private final ScoringService scoringService;

    @Operation(summary= "Gets loan offers", description = "Returns four LoanOfferDTO or message with the reason for rejection of the application")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "CreditDTO successfully created or the application was rejected, but LoanApplicationRequestDTO was valid"),
            @ApiResponse(responseCode = "400", description = "LoanApplicationRequestDTO is not valid")})
    @PostMapping("/conveyor/offers")
    public ResponseEntity<List<LoanOfferDTO>> createLoanOffers(@Valid @RequestBody LoanApplicationRequestDTO loanRequest) {
        List<LoanOfferDTO> offers = prescoringService.createLoanOffers(loanRequest);
        return ResponseEntity.ok(offers);
    }

    @Operation(summary= "Get credit offer", description = "Returns CreditDTO or message with the reason for rejection of the application")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "CreditDTO successfully created or the application was rejected, but ScoringDataDTO was valid"),
            @ApiResponse(responseCode = "400", description = "ScoringDataDTO is not valid")})
    @PostMapping("/conveyor/calculation")
    public ResponseEntity<CreditDTO> createCredit(@Valid @RequestBody ScoringDataDTO scoringDataDTO) {
        CreditDTO credit = scoringService.createCredit(scoringDataDTO);
        return ResponseEntity.ok(credit);
    }
}