package neostudy.conveyor.dto;

import jakarta.validation.constraints.Min;
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

    private EmploymentStatus employmentStatus;

    @Pattern(regexp = "\\d{12}")
    @NotNull
    private String employerINN;

    @NotNull
    @Min(0)
    private BigDecimal salary;

    private Position position;

    @NotNull
    @Min(0)
    private Integer workExperienceTotal;

    @NotNull
    @Min(0)
    private Integer workExperienceCurrent;
}
