package neostudy.deal.feignclient;

import jakarta.validation.Valid;
import neostudy.deal.dto.CreditDTO;
import neostudy.deal.dto.LoanApplicationRequestDTO;
import neostudy.deal.dto.LoanOfferDTO;
import neostudy.deal.dto.ScoringDataDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(value = "jplaceholder", url = "http://localhost:8080/conveyor/")
public interface ConveyorAPIClient {

    @PostMapping("/offers")
    ResponseEntity<List<LoanOfferDTO>> createLoanOffers(LoanApplicationRequestDTO loanRequest);

    @PostMapping("/calculation")
    ResponseEntity<CreditDTO> createCredit(ScoringDataDTO scoringDataDTO);
}