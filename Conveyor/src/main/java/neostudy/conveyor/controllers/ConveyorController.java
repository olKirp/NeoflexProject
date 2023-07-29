package neostudy.conveyor.controllers;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import neostudy.conveyor.dto.CreditDTO;
import neostudy.conveyor.dto.LoanApplicationRequestDTO;
import neostudy.conveyor.dto.LoanOfferDTO;
import neostudy.conveyor.dto.ScoringDataDTO;
import neostudy.conveyor.service.PrescoringService;
import neostudy.conveyor.service.ScoringService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ConveyorController {

    private final PrescoringService prescoringService;

    private final ScoringService scoringService;

    @Autowired
    public ConveyorController(PrescoringService prescoringService, ScoringService scoringService) {
        this.prescoringService = prescoringService;
        this.scoringService = scoringService;
    }

    @Operation(summary= "Gets loan offers", description = "If request is valid returns four offers, otherwise returns empty list")
    @PostMapping("/conveyor/offers")
    public List<LoanOfferDTO> createLoanOffers(@Valid @RequestBody LoanApplicationRequestDTO loanRequest) {
        List<LoanOfferDTO> offers = prescoringService.createLoanOffers(loanRequest);
        return offers;
    }

    @Operation(summary= "Get credit offer")
    @PostMapping("/conveyor/calculation")
    public CreditDTO createCredit(@Valid @RequestBody ScoringDataDTO scoringDataDTO) {
        CreditDTO credit = scoringService.createCredit(scoringDataDTO);
        return credit;
    }
}
