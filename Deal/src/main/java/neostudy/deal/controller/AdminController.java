package neostudy.deal.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import neostudy.deal.dto.ApplicationDTO;
import neostudy.deal.dto.ApplicationStatus;
import neostudy.deal.dto.ChangeType;
import neostudy.deal.exceptions.NotFoundException;
import neostudy.deal.service.ApplicationService;
import neostudy.deal.service.DealService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.openapitools.api.AdminControllerApi;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AdminController implements AdminControllerApi {

    private final ApplicationService applicationService;

    private final DealService dealService;

    private final ModelMapper modelMapper;

    @Override
    public ResponseEntity<List<ApplicationDTO>> getApplications() {
        List<ApplicationDTO> applications = modelMapper.map(applicationService.getApplications(),
                new TypeToken<List<ApplicationDTO>>() {
                }.getType());
        return ResponseEntity.ok(applications);
    }

    @Override
    public ResponseEntity<ApplicationDTO> getApplicationById(@PathVariable Long applicationId) {
        return ResponseEntity.ok(modelMapper.map(
                applicationService.findApplicationById(applicationId).orElseThrow(() -> new NotFoundException("Application " + applicationId + " not found")),
                ApplicationDTO.class));
    }

    @Override
    public ResponseEntity<String> setApplicationStatus(@PathVariable Long applicationId, @RequestBody String status) {
        dealService.setAndSaveApplicationStatus(applicationId, ApplicationStatus.valueOf(status), ChangeType.MANUAL);
        return ResponseEntity.ok("Status was successfully changed");
    }
}
