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

    @NotNull
    private BigDecimal amount;

    @NotNull
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

    @Pattern(regexp = "[A-Za-z0-9_-]+@[A-Za-z0-9_-]+\\.[A-Za-z.]+")
    @NotNull
    private String email;

    @NotNull
    private LocalDate birthdate;

    @Pattern(regexp = "\\d{4}")
    @NotNull
    private String passportSeries;

    @Pattern(regexp = "\\d{6}")
    @NotNull
    private String passportNumber;
}
