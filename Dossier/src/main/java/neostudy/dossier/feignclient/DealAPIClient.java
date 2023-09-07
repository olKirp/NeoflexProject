package neostudy.dossier.feignclient;

import neostudy.dossier.dto.ApplicationDTO;
import neostudy.dossier.dto.enums.ApplicationStatus;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "deal", configuration = FeignClientConfiguration.class)
public interface DealAPIClient {

    @GetMapping("/deal/admin/application/{applicationId}")
    ResponseEntity<ApplicationDTO> getApplicationById(@PathVariable Long applicationId);

    @PutMapping("/deal/admin/application/{applicationId}")
    ResponseEntity setApplicationStatus(@PathVariable Long applicationId, @RequestBody ApplicationStatus status);
}