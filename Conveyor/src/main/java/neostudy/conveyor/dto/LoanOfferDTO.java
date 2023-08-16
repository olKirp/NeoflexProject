package neostudy.conveyor.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class LoanOfferDTO {

    @Schema(description = "ID of user's application", type = "integer", format = "int64", example = "1")
    private Long applicationId;

    @Schema(description = "Requested amount", type = "number", example = "100000")
    private BigDecimal requestedAmount;

    @Schema(description = "Total amount with insurance (if enabled)", type = "number", example = "130000")
    private BigDecimal totalAmount;

    @Schema(description = "Term of loan in months", type = "integer", format = "int32", example = "10")
    private Integer term;

    @Schema(description = "Loan monthly payment", type = "number", example = "13300")
    private BigDecimal monthlyPayment;

    @Schema(description = "Interest rate per annum", type = "number", example = "5")
    private BigDecimal rate;

    @Schema(description = "Is insurance enabled", type = "boolean", example = "true")
    private Boolean isInsuranceEnabled;

    @Schema(description = "Is the user salary client", type = "boolean", example = "true")
    private Boolean isSalaryClient;
}
