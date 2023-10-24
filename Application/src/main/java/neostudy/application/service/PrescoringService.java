package neostudy.application.service;

import lombok.NonNull;
import neostudy.application.dto.LoanApplicationRequestDTO;
import neostudy.application.dto.LoanOfferDTO;

public interface PrescoringService {

    void validateLoanRequest(@NonNull LoanApplicationRequestDTO loanRequest);
    void validateOffer(@NonNull LoanOfferDTO offer);

}
