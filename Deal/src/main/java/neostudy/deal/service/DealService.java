package neostudy.deal.service;

import neostudy.deal.dto.*;

import java.util.List;

public interface DealService {

    void approveLoanOffer(LoanOfferDTO appliedOffer);

    void createCreditForApplication(FinishRegistrationRequestDTO registrationRequest, Long applicationId);

    List<LoanOfferDTO> getLoanOffers(LoanApplicationRequestDTO loanRequest);
}
