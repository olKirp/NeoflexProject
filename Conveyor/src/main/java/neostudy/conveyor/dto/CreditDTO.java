package neostudy.conveyor.dto;

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

    @Schema(description = "Total amount with insurance (if enabled)", example = "130000")
    private BigDecimal amount;

    @Schema(description = "Term of loan in months", example = "10")
    private Integer term;

    @Schema(description = "Loan monthly payment", example = "13300")
    private BigDecimal monthlyPayment;

    @Schema(description = "Interest rate per annum", example = "5")
    private BigDecimal rate;

    @Schema(description = "PSK in percentages", example = "5")
    private BigDecimal psk;

    @Schema(description = "Is insurance enabled", example = "true")
    private Boolean isInsuranceEnabled;

    @Schema(description = "Is the user salary client", example = "true")
    private Boolean isSalaryClient;

    @Schema(description = "Payment schedule")
    private List<PaymentScheduleElement> paymentSchedule;
}
