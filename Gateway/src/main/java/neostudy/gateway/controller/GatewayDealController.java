package neostudy.gateway.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import neostudy.gateway.dto.FinishRegistrationRequestDTO;
import neostudy.gateway.feignclient.DealFeignClient;
import org.openapitools.api.GatewayDealControllerApi;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Controller
@Validated
@RequiredArgsConstructor
public class GatewayDealController implements GatewayDealControllerApi {

    private final DealFeignClient dealFeignClient;

    @Override
    public ResponseEntity<String> sendDocuments(@PathVariable("applicationId") Long applicationId) {
        return dealFeignClient.sendDocuments(applicationId);
    }

    @Override
    public ResponseEntity<String> signDocumentsRequest(@PathVariable("applicationId") Long applicationId) {
        return dealFeignClient.signDocumentsRequest(applicationId);
    }

    @Override
    public ResponseEntity<String> signDocuments(@PathVariable("applicationId") Long applicationId, @RequestBody @Pattern(regexp = "[0-9]{4}") String sesCode) {
        return dealFeignClient.signDocuments(applicationId, sesCode);
    }

    @Override
    public ResponseEntity<String> createCredit(@PathVariable("applicationId") Long applicationId, @Valid @RequestBody FinishRegistrationRequestDTO registrationRequest) {
        return dealFeignClient.createCredit(registrationRequest, applicationId);
    }

}
