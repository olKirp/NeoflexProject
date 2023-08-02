package neostudy.conveyor.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class LoanOfferDTO {

    @Schema(description = "ID of user's application", example = "1")
    private Long applicationId;

    @Schema(description = "Requested amount", example = "100000")
    private BigDecimal requestedAmount;

    @Schema(description = "Total amount with insurance (if enabled)", example = "130000")
    private BigDecimal totalAmount;

    @Schema(description = "Term of loan in months", example = "10")
    private Integer term;

    @Schema(description = "Loan monthly payment", example = "13300")
    private BigDecimal monthlyPayment;

    @Schema(description = "Interest rate per annum", example = "5")
    private BigDecimal rate;

    @Schema(description = "Is insurance enabled", example = "true")
    private Boolean isInsuranceEnabled;

    @Schema(description = "Is the user salary client", example = "true")
    private Boolean isSalaryClient;
}
