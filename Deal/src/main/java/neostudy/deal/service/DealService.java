package neostudy.deal.service;

import lombok.NonNull;
import neostudy.deal.dto.*;
import neostudy.deal.dto.ApplicationStatus;
import neostudy.deal.dto.ChangeType;
import neostudy.deal.dto.Theme;

import java.util.List;

public interface DealService {

    void sendMessage(Long applicationId, @NonNull Theme theme);

    void approveLoanOffer(@NonNull LoanOfferDTO appliedOffer);

    void createCreditForApplication(@NonNull FinishRegistrationRequestDTO registrationRequest, Long applicationId);

    List<LoanOfferDTO> createLoanOffers(@NonNull LoanApplicationRequestDTO loanRequest);

    void setAndSaveApplicationStatus(Long appId, @NonNull ApplicationStatus status, @NonNull ChangeType type);
}
