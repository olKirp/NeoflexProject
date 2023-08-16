package neostudy.conveyor.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class PaymentScheduleElement {

    @Schema(description = "Number of loan payment", type = "integer", format = "int32", example = "1")
    Integer number;

    @Schema(description = "Date of loan payment", type = "number", example = "2023-11-11")
    LocalDate date;

    @Schema(description = "Total amount of payment", type = "number", example = "15000")
    BigDecimal totalPayment;

    @Schema(description = "Amount of interest payment", type = "number", example = "5000")
    BigDecimal interestPayment;

    @Schema(description = "Amount of debt payment", type = "number", example = "10000")
    BigDecimal debtPayment;

    @Schema(description = "Remaining debt after payment ", type = "number", example = "80000")
    BigDecimal remainingDebt;
}
