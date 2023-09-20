package neostudy.gateway.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import neostudy.gateway.dto.LoanApplicationRequestDTO;
import neostudy.gateway.dto.LoanOfferDTO;
import neostudy.gateway.feignclient.ApplicationFeignClient;
import org.openapitools.api.GatewayApplicationControllerApi;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Controller
@Validated
@RequiredArgsConstructor
public class GatewayApplicationController implements GatewayApplicationControllerApi {

    private final ApplicationFeignClient appClient;

    @Override
    public ResponseEntity<List<LoanOfferDTO>> createLoanOffers(@Valid @RequestBody LoanApplicationRequestDTO loanRequest) {
        return appClient.createLoanOffers(loanRequest);
    }

    @Override
    public ResponseEntity<String> saveLoanOffer(@Valid @RequestBody LoanOfferDTO appliedOffer) {
        return appClient.saveLoanOffer(appliedOffer);
    }
}
