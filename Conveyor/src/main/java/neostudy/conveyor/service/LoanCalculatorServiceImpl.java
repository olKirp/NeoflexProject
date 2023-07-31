package neostudy.conveyor.service;

import lombok.RequiredArgsConstructor;
import neostudy.conveyor.dto.PaymentScheduleElement;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanCalculatorServiceImpl implements LoanCalculatorService {

    private final Constants constants;

    public List<PaymentScheduleElement> createPaymentSchedule(BigDecimal amount, BigDecimal rate, int term, LocalDate paymentDate) {
        List<PaymentScheduleElement> paymentSchedule = new ArrayList<>();
        BigDecimal monthlyRate = calculateMonthlyRate(rate);
        BigDecimal totalPayment = calculateMonthlyPayment(amount, rate, term);
        BigDecimal interestPayment;
        BigDecimal debtPayment;

        for (int i = 1; amount.compareTo(BigDecimal.ZERO) > 0; i++) {
            if (amount.compareTo(totalPayment) <= 0) {
                interestPayment = totalPayment.subtract(amount);
                amount = BigDecimal.ZERO;
            } else {
                interestPayment = amount.multiply(monthlyRate);
                amount = amount.subtract(totalPayment.subtract(interestPayment));
            }
            debtPayment = totalPayment.subtract(interestPayment);

            PaymentScheduleElement paymentScheduleElement = PaymentScheduleElement.builder()
                    .interestPayment(interestPayment.setScale(2, RoundingMode.HALF_UP))
                    .remainingDebt(amount.setScale(2, RoundingMode.HALF_UP))
                    .debtPayment(debtPayment.setScale(2, RoundingMode.HALF_UP))
                    .totalPayment(totalPayment.setScale(2, RoundingMode.HALF_UP))
                    .number(i)
                    .date(paymentDate)
                    .build();

            paymentSchedule.add(paymentScheduleElement);
            paymentDate = paymentDate.plusMonths(1);
        }

        return paymentSchedule;
    }

    public BigDecimal calculateMonthlyPayment(BigDecimal amount, BigDecimal rate, int term) {
        BigDecimal monthlyRate = calculateMonthlyRate(rate);
        BigDecimal monthlyRateToThePowerOfTerm = monthlyRate.add(new BigDecimal("1.00")).pow(term).setScale(10, RoundingMode.HALF_UP);
        BigDecimal annuity = monthlyRate.multiply(monthlyRateToThePowerOfTerm).divide(monthlyRateToThePowerOfTerm.subtract(new BigDecimal("1.00")), 10, RoundingMode.HALF_UP);
        return annuity.multiply(amount).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateMonthlyRate(BigDecimal rate) {
        return rate.divide(new BigDecimal("1200.00"), 10, RoundingMode.HALF_UP);
    }

    private BigDecimal getTotalAmount(List<PaymentScheduleElement> paymentSchedule) {
       return paymentSchedule.stream().map(PaymentScheduleElement::getTotalPayment).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getPSK(List<PaymentScheduleElement> paymentSchedule, BigDecimal amount, Integer term) {
        BigDecimal totalAmount = getTotalAmount(paymentSchedule);
        BigDecimal termInYears = new BigDecimal(term).divide(new BigDecimal("12.00"), 10, RoundingMode.HALF_UP);

        return totalAmount.divide(amount, 10, RoundingMode.HALF_UP).subtract(BigDecimal.ONE).divide(termInYears, 10, RoundingMode.HALF_UP).multiply(new BigDecimal("100.00")).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getAmountWithInsurance(boolean isInsurance, BigDecimal amount) {
        if (isInsurance) {
            return amount.add(getInsurancePrice(amount));
        } else {
            return amount;
        }
    }

    private BigDecimal getInsurancePrice(BigDecimal amount) {
        return amount.multiply(constants.getInsurance()).setScale(2, RoundingMode.HALF_UP);
    }
}
