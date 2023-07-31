package neostudy.conveyor.service;

import neostudy.conveyor.dto.PaymentScheduleElement;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class LoanCalculatorServiceImplTest {

    @Autowired
    LoanCalculatorService loanCalculatorService;

    @Test
    void createPaymentSchedule() {
        BigDecimal amount = new BigDecimal("3500000");
        BigDecimal rate = new BigDecimal("7.2");
        int term = 36;

        List<PaymentScheduleElement> paymentSchedule = loanCalculatorService.createPaymentSchedule(amount, rate, term, LocalDate.now());

        assertEquals(term, paymentSchedule.size());
        assertEquals(new BigDecimal("108390.18"), paymentSchedule.get(0).getTotalPayment());
        assertEquals(new BigDecimal("87390.18"), paymentSchedule.get(0).getDebtPayment());
        assertEquals(new BigDecimal("21000.00"), paymentSchedule.get(0).getInterestPayment());
        assertEquals(new BigDecimal("3412609.82"), paymentSchedule.get(0).getRemainingDebt());

        assertEquals(new BigDecimal("108390.18"), paymentSchedule.get(35).getTotalPayment());
        assertEquals(new BigDecimal("107743.62"), paymentSchedule.get(35).getDebtPayment());
        assertEquals(new BigDecimal("646.56"), paymentSchedule.get(35).getInterestPayment());
        assertEquals(new BigDecimal("0.00"), paymentSchedule.get(35).getRemainingDebt());
    }

    @Test
    void getPSK() {
        BigDecimal amount = new BigDecimal("350000.00");
        BigDecimal rate = new BigDecimal("5.00");
        int term = 7;

        List<PaymentScheduleElement> paymentSchedule = loanCalculatorService.createPaymentSchedule(amount, rate, term, LocalDate.now());
        assertEquals(new BigDecimal("2.87"), loanCalculatorService.calculatePSK(paymentSchedule, amount, term));

        amount = new BigDecimal("4500000.00");
        rate = new BigDecimal("11.50");
        term = 24;

        paymentSchedule = loanCalculatorService.createPaymentSchedule(amount, rate, term, LocalDate.now());
        assertEquals(new BigDecimal("6.21"), loanCalculatorService.calculatePSK(paymentSchedule, amount, term));
    }

    @Test
    void calculateMonthlyPayment() {
        BigDecimal amount = new BigDecimal("1500000.00");
        BigDecimal rate = new BigDecimal("5.80");
        int term = 24;

        assertEquals(new BigDecimal("66345.82"), loanCalculatorService.calculateMonthlyPayment(amount, rate, term));
    }

    @Test
    void getAmountWithInsurance() {
        BigDecimal amount = new BigDecimal("100000.00");

        assertEquals(new BigDecimal("130000.00"), loanCalculatorService.calculateAmountWithInsurance(true, amount));
        assertEquals(new BigDecimal("100000.00"), loanCalculatorService.calculateAmountWithInsurance(false, amount));
    }
}
