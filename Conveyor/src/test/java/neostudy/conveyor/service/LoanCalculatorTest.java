package neostudy.conveyor.service;

import neostudy.conveyor.dto.PaymentScheduleElement;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LoanCalculatorTest {

    @Test
    void createPaymentSchedule() {
        BigDecimal amount = new BigDecimal("350000");
        BigDecimal rate = new BigDecimal("5");
        int term = 7;

        List<PaymentScheduleElement> paymentSchedule = LoanCalculator.createPaymentSchedule(amount, rate, term, LocalDate.now());
        paymentSchedule.forEach(System.out::println);
        assertEquals(term, paymentSchedule.size());
    }

}
