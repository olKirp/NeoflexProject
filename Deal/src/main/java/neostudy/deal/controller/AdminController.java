package neostudy.deal.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import neostudy.deal.dto.ApplicationDTO;
import neostudy.deal.dto.ApplicationStatus;
import neostudy.deal.dto.enums.ChangeType;
import neostudy.deal.service.ApplicationService;
import neostudy.deal.service.DealService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AdminController {

    private final ApplicationService applicationService;

    private final DealService dealService;

    private final ModelMapper modelMapper;

    @GetMapping("/deal/admin/application")
    @Operation(summary = "Get applications", description = "Finds all applications and returns it")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Applications successfully returned")
    })
    public ResponseEntity<List<ApplicationDTO>> getApplications() {
        List<ApplicationDTO> applications = modelMapper.map(applicationService.getApplications(),
                new TypeToken<List<ApplicationDTO>> () {}.getType());
        return ResponseEntity.ok(applications);
    }


    @GetMapping("/deal/admin/application/{applicationId}")
    @Operation(summary = "Get application", description = "Finds application by id and returns it")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Application successfully returned"),
            @ApiResponse(responseCode = "404", description = "Application not found")
    })
    public ResponseEntity<ApplicationDTO> getApplicationById(@PathVariable Long applicationId) {
        return ResponseEntity.ok(modelMapper.map(applicationService.getApplicationById(applicationId), ApplicationDTO.class));
    }


    @PutMapping("/deal/admin/application/{applicationId}")
    @Operation(summary = "Set application status", description = "Set specified status to an application")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status was successfully changed"),
            @ApiResponse(responseCode = "400", description = "Bad request (includes incorrect status)"),
            @ApiResponse(responseCode = "404", description = "Application not found")
    })
    public ResponseEntity setApplicationStatus(@PathVariable Long applicationId, @RequestBody ApplicationStatus status) {
        dealService.setApplicationStatus(applicationId, status, ChangeType.MANUAL);
        return new ResponseEntity(HttpStatus.OK);
    }




}
