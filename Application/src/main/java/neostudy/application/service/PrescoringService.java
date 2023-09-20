package neostudy.application.service;

import neostudy.application.dto.LoanApplicationRequestDTO;
import neostudy.application.dto.LoanOfferDTO;

public interface PrescoringService {

    void validateLoanRequest(LoanApplicationRequestDTO loanRequest);
    void validateOffer(LoanOfferDTO offer);

}
