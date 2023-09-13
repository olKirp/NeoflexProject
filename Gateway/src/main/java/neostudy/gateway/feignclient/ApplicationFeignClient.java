package neostudy.gateway.feignclient;

import jakarta.validation.Valid;
import neostudy.gateway.dto.LoanApplicationRequestDTO;
import neostudy.gateway.dto.LoanOfferDTO;
import org.springframework.cloud.openfeign.FeignClient;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "application")
public interface ApplicationFeignClient {

    @PostMapping("/application")
    ResponseEntity<List<LoanOfferDTO>> createLoanOffers(@Valid @RequestBody LoanApplicationRequestDTO loanRequest);

    @PostMapping("/application/offer")
    void saveLoanOffer(@Valid @RequestBody LoanOfferDTO appliedOffer);
}