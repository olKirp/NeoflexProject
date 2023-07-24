package neostudy.conveyor.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoanApplicationRequestDTO {

    private BigDecimal amount;

    private Integer term;

    @Size(min = 2, max = 30)
    @Pattern(regexp = "[A-Z][a-zA-Z]*")
    @NotNull
    private String firstName;

    @Size(min = 2, max = 30)
    @Pattern(regexp = "[A-Z][a-zA-Z]*")
    @NotNull
    private String lastName;

    @Size(min = 2, max = 30)
    @Pattern(regexp = "[A-Z][a-zA-Z]*")
    @NotNull
    private String middleName;

    private String email;

    private LocalDate birthdate;

    @Pattern(regexp = "\\d{4}")
    private String passportSeries;

    @Pattern(regexp = "\\d{6}")
    private String passportNumber;
}
