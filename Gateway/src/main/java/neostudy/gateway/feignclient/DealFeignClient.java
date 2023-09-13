package neostudy.gateway.feignclient;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import neostudy.gateway.dto.FinishRegistrationRequestDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "deal")
public interface DealFeignClient {

    @PutMapping("/deal/calculate/{applicationId}")
    void createCredit(@Valid @RequestBody FinishRegistrationRequestDTO registrationRequest,
                             @Parameter(name = "applicationId",
                                     description = "ID of application",
                                     example = "1",
                                     required = true)
                             @PathVariable("applicationId") Long applicationId);


    @PostMapping("/{applicationId}/send")
    void sendDocuments(@PathVariable("applicationId") Long applicationId);

    @PostMapping("/{applicationId}/sign")
    void signDocumentsRequest(@PathVariable("applicationId") Long applicationId);

    @PostMapping("/{applicationId}/code")
    void signDocuments(@PathVariable("applicationId") Long applicationId, @RequestBody @Pattern(regexp = "[0-9]{4}") String sesCode);

}
