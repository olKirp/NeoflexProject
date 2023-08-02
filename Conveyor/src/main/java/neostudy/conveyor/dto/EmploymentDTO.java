package neostudy.conveyor.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import neostudy.conveyor.dto.enums.EmploymentStatus;
import neostudy.conveyor.dto.enums.Position;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmploymentDTO {

    @NotNull
    @Schema(description = "User's employment status", example = "EMPLOYEE")
    private EmploymentStatus employmentStatus;

    @NotBlank
    @Pattern(regexp = "\\d{12}")
    @Schema(description = "User's INN", example = "012345678912")
    private String employerINN;

    @Min(0)
    @NotNull
    @Schema(description = "User's salary", example = "50000")
    private BigDecimal salary;

    @NotNull
    @Schema(description = "User's position", example = "MIDDLE_MANAGER")
    private Position position;

    @Min(0)
    @NotNull
    @Schema(description = "User's total work experience in months", example = "36")
    private Integer workExperienceTotal;

    @Min(0)
    @NotNull
    @Schema(description = "User's current work experience in months", example = "12")
    private Integer workExperienceCurrent;
}
