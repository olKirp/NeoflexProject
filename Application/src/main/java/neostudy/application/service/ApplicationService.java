package neostudy.application.service;

import neostudy.application.dto.LoanApplicationRequestDTO;
import neostudy.application.dto.LoanOfferDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ApplicationService {

    ResponseEntity<List<LoanOfferDTO>> createLoanOffers(LoanApplicationRequestDTO loanRequest);

    ResponseEntity<String> applyLoanOffer(LoanOfferDTO appliedOffer);

}
