package neostudy.deal.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import neostudy.deal.dto.enums.Gender;
import neostudy.deal.dto.enums.MaritalStatus;
import neostudy.deal.dto.validation.PastConstraint;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinishRegistrationRequestDTO {

    @NotNull
    @Schema(description = "User's gender", type = "String", example = "FEMALE")
    private Gender gender;

    @NotNull
    @Schema(description = "User's marital status", type = "String", example = "SINGLE")
    private MaritalStatus maritalStatus;

    @Min(0)
    @NotNull
    @Schema(description = "Number of dependent's", type = "integer", format = "int32", example = "0")
    private Integer dependentAmount;

    @NotNull
    @Schema(description = "Description of employment status", type = "object")
    private EmploymentDTO employmentDTO;

    @NotNull
    @PastConstraint
    @Schema(description = "Passport issue date. Should be before today", type = "String", example = "2001-01-01")
    private LocalDate passportIssueDate;

    @NotBlank
    @Schema(description = "Passport issue branch", type = "String", example = "Department of Internal Affairs in Moscow")
    private String passportIssueBranch;

    @NotBlank
    @Pattern(regexp = "[0-9]{10}")
    @Schema(description = "User's account", type = "String", example = "0123456789")
    private String account;
}
