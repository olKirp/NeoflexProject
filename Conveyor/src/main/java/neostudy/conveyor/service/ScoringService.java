package neostudy.conveyor.service;

import neostudy.conveyor.dto.PaymentScheduleElement;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Service
public class ScoringService {

    public static final BigDecimal MIN_AMOUNT;
    public static final BigDecimal MAX_AMOUNT;

    public static final int MIN_TERM;
    public static final int MAX_TERM;

    public static final BigDecimal BASE_RATE;

    public static final BigDecimal DISCOUNT_FOR_SALARY_CLIENT;
    public static final BigDecimal DISCOUNT_FOR_INSURANCE;
    public static final BigDecimal PENALTY_FOR_NO_INSURANCE;


    static {
        BigDecimal penaltyForNoInsurance;
        BigDecimal discountForInsurance;
        BigDecimal discountForSalaryClient;
        BigDecimal baseRate;
        int maxTerm;
        int minTerm;
        BigDecimal maxAmount;
        BigDecimal minAmount;
        Properties properties = new Properties();
        try {
            properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("constants.properties"));

            minAmount = new BigDecimal(properties.getProperty("minAmount"));
            maxAmount = new BigDecimal(properties.getProperty("maxAmount"));
            minTerm = Integer.parseInt(properties.getProperty("minTerm"));
            maxTerm = Integer.parseInt(properties.getProperty("maxTerm"));
            baseRate = new BigDecimal(properties.getProperty("baseRate"));
            discountForSalaryClient = new BigDecimal(properties.getProperty("discountForSalaryClient"));
            discountForInsurance = new BigDecimal(properties.getProperty("discountForInsurance"));
            penaltyForNoInsurance = new BigDecimal(properties.getProperty("penaltyForNoInsurance"));
        } catch (Exception e) {
            minAmount = new BigDecimal(10000);
            maxAmount = new BigDecimal(7000000);
            minTerm = 6;
            maxTerm = 60;
            baseRate = new BigDecimal(10);
            discountForSalaryClient = new BigDecimal("2.5");
            discountForInsurance = new BigDecimal("1.7");
            penaltyForNoInsurance = new BigDecimal("3.0");
            e.printStackTrace();
        }
        PENALTY_FOR_NO_INSURANCE = penaltyForNoInsurance;
        DISCOUNT_FOR_INSURANCE = discountForInsurance;
        DISCOUNT_FOR_SALARY_CLIENT = discountForSalaryClient;
        BASE_RATE = baseRate;
        MAX_TERM = maxTerm;
        MIN_TERM = minTerm;
        MAX_AMOUNT = maxAmount;
        MIN_AMOUNT = minAmount;
    }


    public List<PaymentScheduleElement> createPaymentSchedule(BigDecimal amount, BigDecimal rate, int term, LocalDate paymentDate) {
        List<PaymentScheduleElement> paymentSchedule = new ArrayList<>();
        BigDecimal monthlyRate = rate.divide(new BigDecimal("1200.00"), 10, RoundingMode.HALF_UP);
        BigDecimal totalPayment = countMonthlyPayment(amount, rate, term);
        BigDecimal interestPayment;
        BigDecimal debtPayment;
        for (int i = 1;amount.compareTo(new BigDecimal("0.00")) > 0; i++) {
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

    public BigDecimal countMonthlyPayment(BigDecimal amount, BigDecimal rate, int term) {
        BigDecimal monthlyRate = rate.divide(new BigDecimal("1200.00"), 10, RoundingMode.HALF_UP);
        BigDecimal monthlyRateToThePowerOfTerm = monthlyRate.add(new BigDecimal("1.00")).pow(term).setScale(10, RoundingMode.HALF_UP);
        BigDecimal annuity = monthlyRate.multiply(monthlyRateToThePowerOfTerm).divide(monthlyRateToThePowerOfTerm.subtract(new BigDecimal("1.00")), 10, RoundingMode.HALF_UP);
        return annuity.multiply(amount).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getTotalAmountOfInterest(List<PaymentScheduleElement> paymentSchedule) {
        BigDecimal sum = new BigDecimal("0.00");
        for (PaymentScheduleElement paymentScheduleElement : paymentSchedule) {
            sum = sum.add(paymentScheduleElement.getInterestPayment());
        }
        return sum;
    }

}
