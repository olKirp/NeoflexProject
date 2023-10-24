package neostudy.application.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import neostudy.application.dto.LoanApplicationRequestDTO;
import neostudy.application.dto.LoanOfferDTO;
import neostudy.application.service.ApplicationService;
import org.openapitools.api.ApplicationApi;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ApplicationController implements ApplicationApi {

    private final ApplicationService applicationServiceImpl;

    @Override
    public ResponseEntity<List<LoanOfferDTO>> createLoanOffers(@Valid @RequestBody LoanApplicationRequestDTO loanRequest) {
        return applicationServiceImpl.createLoanOffers(loanRequest);
    }

    @Override
    public ResponseEntity<String> saveLoanOffer(@Valid @RequestBody LoanOfferDTO appliedOffer) {
        return applicationServiceImpl.applyLoanOffer(appliedOffer);
    }
}
