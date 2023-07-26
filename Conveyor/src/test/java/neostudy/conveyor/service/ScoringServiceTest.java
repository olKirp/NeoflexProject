package neostudy.conveyor.service;

import neostudy.conveyor.dto.PaymentScheduleElement;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ScoringServiceTest {

    @Autowired
    ScoringService scoringService;

    @Test
    void createPaymentSchedule() {
        BigDecimal amount = new BigDecimal("350000");
        BigDecimal rate = new BigDecimal("5");
        int term = 7;

        List<PaymentScheduleElement> paymentSchedule = scoringService.createPaymentSchedule(amount, rate, term, LocalDate.now());
        paymentSchedule.forEach(System.out::println);
        assertEquals(term, paymentSchedule.size());
    }

}