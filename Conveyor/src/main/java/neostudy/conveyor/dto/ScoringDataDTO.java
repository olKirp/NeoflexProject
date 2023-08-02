package neostudy.conveyor.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import neostudy.conveyor.dto.enums.Gender;
import neostudy.conveyor.dto.enums.MaritalStatus;
import neostudy.conveyor.validation.PastConstraint;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScoringDataDTO {

    @NotNull
    @Schema(description = "Requested loan amount", example = "100000")
    private BigDecimal amount;

    @NotNull
    @Schema(description = "Term of loan in months", example = "10")
    private Integer term;

    @NotBlank
    @Size(min = 2, max = 30)
    @Pattern(regexp = "[A-Z][a-zA-Z]*")
    @Schema(description = "User's firstname", example = "Anna")
    private String firstName;

    @NotBlank
    @Size(min = 2, max = 30)
    @Pattern(regexp = "[A-Z][a-zA-Z]*")
    @Schema(description = "User's lastname", example = "Petrova")
    private String lastName;

    @NotBlank
    @Size(min = 2, max = 30)
    @Pattern(regexp = "[A-Z][a-zA-Z]*")
    @Schema(description = "User's middle name", example = "Sergeevna")
    private String middleName;

    @NotNull
    @Schema(description = "User's gender", example = "FEMALE")
    private Gender gender;

    @NotNull
    @PastConstraint
    @Schema(description = "User's birthdate. Should be before today", example = "1990-01-01")
    private LocalDate birthdate;

    @NotBlank
    @Pattern(regexp = "\\d{4}")
    @Schema(description = "User's passport series", example = "1111")
    private String passportSeries;

    @NotBlank
    @Pattern(regexp = "\\d{6}")
    @Schema(description = "User's passport number", example = "222222")
    private String passportNumber;

    @NotNull
    @PastConstraint
    @Schema(description = "Passport issue date. Should be before today", example = "2001-01-01")
    private LocalDate passportIssueDate;

    @NotBlank
    @Schema(description = "Passport issue branch", example = "Department of Internal Affairs in Moscow")
    private String passportIssueBranch;

    @NotNull
    @Schema(description = "User's marital status", example = "SINGLE")
    private MaritalStatus maritalStatus;

    @Min(0)
    @Schema(description = "Number of dependent's", example = "0")
    private Integer dependentAmount;

    @NotNull
    @Schema(description = "Description of employment status")
    private EmploymentDTO employment;

    @NotBlank
    @Pattern(regexp = "[0-9]{10}")
    @Schema(description = "User's account", example = "0123456789")
    private String account;

    @NotNull
    @Schema(description = "Does the user wish to purchase insurance", example = "true")
    private Boolean isInsuranceEnabled;

    @NotNull
    @Schema(description = "Is the user a salary client", example = "false")
    private Boolean isSalaryClient;
}
