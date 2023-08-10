package neostudy.deal.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import neostudy.deal.dto.enums.EmploymentPosition;
import neostudy.deal.dto.enums.EmploymentStatus;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmploymentDTO {

    @NotNull
    @Schema(description = "User's employment status", type = "string", example = "EMPLOYED")
    private EmploymentStatus status;

    @NotBlank
    @Pattern(regexp = "\\d{12}")
    @Schema(description = "User's INN", type = "string", example = "012345678912")
    private String employerINN;

    @Min(0)
    @NotNull
    @Schema(description = "User's salary", type = "number", example = "50000")
    private BigDecimal salary;

    @NotNull
    @Schema(description = "User's position", type = "string", example = "MID_MANAGER")
    private EmploymentPosition employmentPosition;

    @Min(0)
    @NotNull
    @Schema(description = "User's total work experience in months", type = "integer", format = "int32", example = "36")
    private Integer workExperienceTotal;

    @Min(0)
    @NotNull
    @Schema(description = "User's current work experience in months", type = "integer", format = "int32", example = "12")
    private Integer workExperienceCurrent;
}
