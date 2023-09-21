package neostudy.deal.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import neostudy.deal.dto.FinishRegistrationRequestDTO;
import neostudy.deal.dto.LoanApplicationRequestDTO;
import neostudy.deal.dto.LoanOfferDTO;
import neostudy.deal.dto.Theme;
import neostudy.deal.service.*;
import org.openapitools.api.DealControllerApi;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class DealController implements DealControllerApi {

    private final DealService dealService;

    @Override
    public ResponseEntity<List<LoanOfferDTO>> createLoanOffers(@Valid @RequestBody LoanApplicationRequestDTO loanRequest) {
        List<LoanOfferDTO> offers = dealService.createLoanOffers(loanRequest);
        return ResponseEntity.ok(offers);
    }

    @Override
    public ResponseEntity<String> saveLoanOffer(@Valid @RequestBody LoanOfferDTO appliedOffer) {
        dealService.approveLoanOffer(appliedOffer);
        dealService.sendMessage(appliedOffer.getApplicationId(), Theme.FINISH_REGISTRATION);
        return ResponseEntity.ok("Loan offer was saved");
    }

    @Override
    public ResponseEntity<String> createCredit(@PathVariable("applicationId") Long applicationId, @Valid @RequestBody FinishRegistrationRequestDTO registrationRequest) {
        dealService.createCreditForApplication(registrationRequest, applicationId);
        dealService.sendMessage(applicationId, Theme.CREATE_DOCUMENTS);
        return null;
    }
}