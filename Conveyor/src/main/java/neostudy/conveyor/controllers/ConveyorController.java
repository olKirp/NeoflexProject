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
import neostudy.conveyor.service.LoanOffersService;
import neostudy.conveyor.service.ScoringService;
import org.openapitools.api.ConveyorApi;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Log4j2
@RestController
@RequiredArgsConstructor
@Tag(name="Conveyor controller")
public class ConveyorController implements ConveyorApi{

    private final LoanOffersService loanOffersService;

    private final ScoringService scoringService;

    @Override
    public ResponseEntity<List<LoanOfferDTO>> createLoanOffers(@Valid @RequestBody LoanApplicationRequestDTO loanRequest) {
        List<LoanOfferDTO> offers = loanOffersService.createLoanOffers(loanRequest);
        System.out.println("offers " + offers);
        return ResponseEntity.ok(offers);
    }

    @Override
    public ResponseEntity<CreditDTO> createCredit(@Valid @RequestBody ScoringDataDTO scoringDataDTO) {
        CreditDTO credit = scoringService.createCredit(scoringDataDTO);
        return ResponseEntity.ok(credit);
    }
}