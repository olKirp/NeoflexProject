package neostudy.application.feignclient;

import jakarta.validation.Valid;
import neostudy.application.dto.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "deal")
public interface DealAPIClient {

    @PostMapping("/deal/application")
    ResponseEntity<List<LoanOfferDTO>> createLoanOffers(LoanApplicationRequestDTO loanRequest);

    @PutMapping("/deal/offer")
    ResponseEntity<String> saveLoanOffer(@Valid @RequestBody LoanOfferDTO appliedOffer);
}