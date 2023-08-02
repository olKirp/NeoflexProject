package neostudy.conveyor.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import neostudy.conveyor.validation.PastConstraint;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoanApplicationRequestDTO {

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

    @NotBlank
    @Pattern(regexp = "[A-Za-z0-9_-]+@[A-Za-z0-9_-]+\\.[A-Za-z.]+")
    @Schema(description = "User's email", example = "example@mail.ru")
    private String email;

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
}
