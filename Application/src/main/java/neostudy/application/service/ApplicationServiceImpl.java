package neostudy.application.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import neostudy.application.dto.LoanApplicationRequestDTO;
import neostudy.application.dto.LoanOfferDTO;
import neostudy.application.feignclient.DealAPIClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ApplicationServiceImpl implements ApplicationService {

    private final DealAPIClient apiClient;

    private final PrescoringService prescoringService;

    public ResponseEntity<List<LoanOfferDTO>> createLoanOffers(@NonNull LoanApplicationRequestDTO loanRequest) {
        prescoringService.validateLoanRequest(loanRequest);
        return apiClient.createLoanOffers(loanRequest);
    }

    public ResponseEntity<String> applyLoanOffer(@NonNull LoanOfferDTO appliedOffer) {
        prescoringService.validateOffer(appliedOffer);
        return apiClient.saveLoanOffer(appliedOffer);
    }

}
