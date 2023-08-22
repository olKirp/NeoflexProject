package neostudy.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoanOfferDTO {

    @NotNull
    @Min(0)
    @Schema(description = "ID of user's application", type = "integer", format = "int64", example = "1")
    private Long applicationId;

    @NotNull
    @Min(0)
    @Schema(description = "Requested amount", type = "number", example = "100000")
    private BigDecimal requestedAmount;

    @NotNull
    @Min(0)
    @Schema(description = "Total amount with insurance (if enabled)", type = "number", example = "130000")
    private BigDecimal totalAmount;

    @NotNull
    @Min(0)
    @Schema(description = "Term of loan in months", type = "integer", format = "int32", example = "10")
    private Integer term;

    @NotNull
    @Min(0)
    @Schema(description = "Loan monthly payment", type = "number", example = "13300")
    private BigDecimal monthlyPayment;

    @NotNull
    @Min(0)
    @Schema(description = "Interest rate per annum", type = "number", example = "5")
    private BigDecimal rate;

    @NotNull
    @Schema(description = "Is insurance enabled", type = "boolean", example = "true")
    private Boolean isInsuranceEnabled;

    @NotNull
    @Schema(description = "Is the user salary client", type = "boolean", example = "true")
    private Boolean isSalaryClient;
}
