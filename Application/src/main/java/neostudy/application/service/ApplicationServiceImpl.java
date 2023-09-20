package neostudy.application.service;

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

    public ResponseEntity<List<LoanOfferDTO>> createLoanOffers(LoanApplicationRequestDTO loanRequest) {
        prescoringService.validateLoanRequest(loanRequest);
        System.out.println("Return : " + apiClient.createLoanOffers(loanRequest));
        return apiClient.createLoanOffers(loanRequest);
    }

    public ResponseEntity<String> applyLoanOffer(LoanOfferDTO appliedOffer) {
        prescoringService.validateOffer(appliedOffer);
        return apiClient.saveLoanOffer(appliedOffer);
    }

}
