package neostudy.deal.service;

import neostudy.deal.dto.*;
import neostudy.deal.dto.ApplicationStatus;
import neostudy.deal.dto.enums.ChangeType;
import neostudy.deal.dto.Theme;

import java.util.List;

public interface DealService {

    void sendMessage(Long applicationId, Theme theme);

    void approveLoanOffer(LoanOfferDTO appliedOffer);

    void createCreditForApplication(FinishRegistrationRequestDTO registrationRequest, Long applicationId);

    List<LoanOfferDTO> createLoanOffera(LoanApplicationRequestDTO loanRequest);

    void setApplicationStatus(Long appId, ApplicationStatus status, ChangeType type);
}
