package neostudy.deal.feignclient;

import neostudy.deal.dto.CreditDTO;
import neostudy.deal.dto.LoanApplicationRequestDTO;
import neostudy.deal.dto.LoanOfferDTO;
import neostudy.deal.dto.ScoringDataDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@FeignClient(name = "conveyor")
public interface ConveyorAPIClient {


    @PostMapping("/conveyor/offers")
    ResponseEntity<List<LoanOfferDTO>> createLoanOffers(LoanApplicationRequestDTO loanRequest);

    @PostMapping("/conveyor/calculation")
    ResponseEntity<CreditDTO> createCredit(ScoringDataDTO scoringDataDTO);
}