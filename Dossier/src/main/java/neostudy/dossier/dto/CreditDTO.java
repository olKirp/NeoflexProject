package neostudy.dossier.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreditDTO {

    @Schema(description = "Total amount with insurance (if enabled)", type = "number", example = "130000")
    private BigDecimal amount;

    @Schema(description = "Term of loan in months", type = "integer", format = "int32", example = "10")
    private Integer term;

    @Schema(description = "Loan monthly payment", type = "number", example = "13300")
    private BigDecimal monthlyPayment;

    @Schema(description = "Interest rate per annum", type = "number", example = "5")
    private BigDecimal rate;

    @Schema(description = "PSK in percentages", type = "number", example = "5")
    private BigDecimal psk;

    @Schema(description = "Is insurance enabled", type = "boolean", example = "true")
    private Boolean isInsuranceEnabled;

    @Schema(description = "Is the user salary client", type = "boolean", example = "true")
    private Boolean isSalaryClient;

    @Schema(description = "Payment schedule", type = "array")
    private List<PaymentScheduleElement> paymentSchedule;
}
