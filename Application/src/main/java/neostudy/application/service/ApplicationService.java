package neostudy.application.service;

import lombok.NonNull;
import neostudy.application.dto.LoanApplicationRequestDTO;
import neostudy.application.dto.LoanOfferDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ApplicationService {

    ResponseEntity<List<LoanOfferDTO>> createLoanOffers(@NonNull LoanApplicationRequestDTO loanRequest);

    ResponseEntity<String> applyLoanOffer(@NonNull LoanOfferDTO appliedOffer);
}
