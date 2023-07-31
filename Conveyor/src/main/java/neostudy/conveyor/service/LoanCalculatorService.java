package neostudy.conveyor.service;

import neostudy.conveyor.dto.PaymentScheduleElement;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface LoanCalculatorService {

    List<PaymentScheduleElement> createPaymentSchedule(BigDecimal amount, BigDecimal rate, int term, LocalDate paymentDate);

    BigDecimal calculateMonthlyPayment(BigDecimal amount, BigDecimal rate, int term);

    BigDecimal getPSK(List<PaymentScheduleElement> paymentSchedule, BigDecimal amount, Integer term);

    BigDecimal getAmountWithInsurance(boolean isInsurance, BigDecimal amount);
}
