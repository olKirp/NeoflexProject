package neostudy.conveyor.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class PaymentScheduleElement {

    Integer number;

    LocalDate date;

    BigDecimal totalPayment;

    BigDecimal interestPayment;

    BigDecimal debtPayment;

    BigDecimal remainingDebt;
}
