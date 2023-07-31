package neostudy.conveyor.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import neostudy.conveyor.dto.enums.Gender;
import neostudy.conveyor.dto.enums.MaritalStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScoringDataDTO {

    @NotNull
    private BigDecimal amount;

    @NotNull
    private Integer term;

    @Size(min = 2, max = 30)
    @Pattern(regexp = "[A-Z][a-zA-Z]*")
    @NotBlank
    private String firstName;

    @Size(min = 2, max = 30)
    @Pattern(regexp = "[A-Z][a-zA-Z]*")
    @NotBlank
    private String lastName;

    @Size(min = 2, max = 30)
    @Pattern(regexp = "[A-Z][a-zA-Z]*")
    @NotBlank
    private String middleName;

    @NotNull
    private Gender gender;

    @NotNull
    private LocalDate birthdate;

    @Pattern(regexp = "\\d{4}")
    @NotBlank
    private String passportSeries;

    @Pattern(regexp = "\\d{6}")
    @NotBlank
    private String passportNumber;

    @NotNull
    private LocalDate passportIssueDate;

    @NotBlank
    private String passportIssueBranch;

    @NotNull
    private MaritalStatus maritalStatus;

    @Min(0)
    private Integer dependentAmount;

    @NotNull
    private EmploymentDTO employment;

    @NotBlank
    @Pattern(regexp = "[0-9]{10}")
    private String account;

    @NotNull
    private Boolean isInsuranceEnabled;

    @NotNull
    private Boolean isSalaryClient;
}
