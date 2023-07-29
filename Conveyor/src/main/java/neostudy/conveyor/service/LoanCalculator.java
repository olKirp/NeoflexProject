package neostudy.conveyor.service;

import neostudy.conveyor.dto.PaymentScheduleElement;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LoanCalculator {

    public static List<PaymentScheduleElement> createPaymentSchedule(BigDecimal amount, BigDecimal rate, int term, LocalDate paymentDate) {
        List<PaymentScheduleElement> paymentSchedule = new ArrayList<>();
        BigDecimal monthlyRate = calculateMonthlyRate(rate);
        BigDecimal totalPayment = countMonthlyPayment(amount, rate, term);
        BigDecimal interestPayment;
        BigDecimal debtPayment;

        for (int i = 1; amount.compareTo(new BigDecimal("0.00")) > 0; i++) {
            if (amount.compareTo(totalPayment) <= 0) {
                interestPayment = totalPayment.subtract(amount);
                amount = new BigDecimal("0.00");
            } else {
                interestPayment = amount.multiply(monthlyRate);
                amount = amount.subtract(totalPayment.subtract(interestPayment));
            }
            debtPayment = totalPayment.subtract(interestPayment);

            PaymentScheduleElement paymentScheduleElement = new PaymentScheduleElement();
            paymentScheduleElement.setInterestPayment(interestPayment.setScale(2, RoundingMode.HALF_UP));
            paymentScheduleElement.setRemainingDebt(amount.setScale(2, RoundingMode.HALF_UP));
            paymentScheduleElement.setDebtPayment(debtPayment.setScale(2, RoundingMode.HALF_UP));
            paymentScheduleElement.setTotalPayment(totalPayment.setScale(2, RoundingMode.HALF_UP));
            paymentScheduleElement.setNumber(i);
            paymentScheduleElement.setDate(paymentDate);

            paymentSchedule.add(paymentScheduleElement);
            paymentDate = paymentDate.plusMonths(1);
        }

        return paymentSchedule;
    }

    public static BigDecimal countMonthlyPayment(BigDecimal amount, BigDecimal rate, int term) {
        BigDecimal monthlyRate = calculateMonthlyRate(rate);
        BigDecimal monthlyRateToThePowerOfTerm = monthlyRate.add(new BigDecimal("1.00")).pow(term).setScale(10, RoundingMode.HALF_UP);
        BigDecimal annuity = monthlyRate.multiply(monthlyRateToThePowerOfTerm).divide(monthlyRateToThePowerOfTerm.subtract(new BigDecimal("1.00")), 10, RoundingMode.HALF_UP);
        return annuity.multiply(amount).setScale(2, RoundingMode.HALF_UP);
    }

    private static BigDecimal calculateMonthlyRate(BigDecimal rate) {
        return rate.divide(new BigDecimal("1200.00"), 10, RoundingMode.HALF_UP);
    }

    public static BigDecimal getTotalAmountOfInterest(List<PaymentScheduleElement> paymentSchedule) {
        BigDecimal sum = new BigDecimal("0.00");
        for (PaymentScheduleElement paymentScheduleElement : paymentSchedule) {
            sum = sum.add(paymentScheduleElement.getInterestPayment());
        }
        return sum;
    }
}
