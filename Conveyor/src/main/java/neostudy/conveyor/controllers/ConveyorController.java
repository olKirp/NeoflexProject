package neostudy.conveyor.controllers;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import neostudy.conveyor.dto.LoanApplicationRequestDTO;
import neostudy.conveyor.dto.LoanOfferDTO;
import neostudy.conveyor.service.ConveyorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ConveyorController {

    private final ConveyorService conveyorService;

    @Autowired
    public ConveyorController(ConveyorService conveyorService) {
        this.conveyorService = conveyorService;
    }

    @Operation(summary= "Gets loan offers", description = "If request is valid returns four offers, otherwise returns empty list")
    @PostMapping("/conveyor/offers")
    public List<LoanOfferDTO> createLoanOffers(@Valid @RequestBody LoanApplicationRequestDTO loanRequest) {
        List<LoanOfferDTO> offers = conveyorService.createLoanOffers(loanRequest);
        return offers;
    }
}
