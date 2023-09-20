package neostudy.application.service;

import neostudy.application.dto.LoanApplicationRequestDTO;
import neostudy.application.dto.LoanOfferDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PrescoringServiceImplTest {

    @Autowired
    PrescoringServiceImpl prescoringService;

    private LoanApplicationRequestDTO request;

    private LoanOfferDTO offer;

    @Value("${constants.minTerm}")
    private Integer minTerm;

    @Value("${constants.maxTerm}")
    private Integer maxTerm;

    @Value("${constants.minAmount}")
    private BigDecimal minAmount;

    @Value("${constants.maxAmount}")
    private BigDecimal maxAmount;

    @BeforeEach
    void createLoanApplicationRequest() {
        request = new LoanApplicationRequestDTO(new BigDecimal("100000.0"), 10, "Name", "Lastname", "Middlename", "correct@mail.ru", LocalDate.of(1980, 10, 10), "1111", "222222");
        offer = LoanOfferDTO
                .builder()
                .term(10)
                .requestedAmount(new BigDecimal("100000"))
                .totalAmount(new BigDecimal("120000"))
                .monthlyPayment(new BigDecimal("9000"))
                .rate(new BigDecimal("5.00"))
                .build();
    }

    @Test
    void givenIncorrectAmountInRequest_ThenThrowException() {
        String correctMsg = "Requested amount less than " + minAmount + " or bigger than " + maxAmount;

        request.setAmount(maxAmount.add(BigDecimal.ONE));
        Exception exception = assertThrows(IllegalArgumentException.class, () -> prescoringService.validateLoanRequest(request));
        assertEquals(correctMsg, exception.getMessage());

        request.setAmount(minAmount.subtract(BigDecimal.ONE));
        exception = assertThrows(IllegalArgumentException.class, () -> prescoringService.validateLoanRequest(request));
        assertEquals(correctMsg, exception.getMessage());
    }

    @Test
    void givenAgeLessThanEighteenInRequest_ThenThrowException() {
        String correctMsg = "User younger than 18";

        request.setBirthdate(LocalDate.of(2010, 1, 1));
        Exception exception = assertThrows(IllegalArgumentException.class, () -> prescoringService.validateLoanRequest(request));
        assertEquals(correctMsg, exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(ints = {5, 61})
    void givenIncorrectTermInRequest_ThenThrowException(int term) {
        String correctMsg = "Term longer than " + maxTerm + " or shorter than " + minTerm;

        request.setTerm(term);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> prescoringService.validateLoanRequest(request));
        assertEquals(correctMsg, exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(ints = {5, 61})
    void givenIncorrectTermInOffer_ThenThrowException(int term) {
        String correctMsg = "Term longer than " + maxTerm + " or shorter than " + minTerm;

        offer.setTerm(term);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> prescoringService.validateOffer(offer));
        assertEquals(correctMsg, exception.getMessage());
    }

    @Test
    void givenIncorrectAmountInOffer_ThenThrowException() {
        String correctMsg = "Requested amount less than " + minAmount + " or bigger than " + maxAmount;

        offer.setRequestedAmount(maxAmount.add(BigDecimal.ONE));
        Exception exception = assertThrows(IllegalArgumentException.class, () -> prescoringService.validateOffer(offer));
        assertEquals(correctMsg, exception.getMessage());


        offer.setRequestedAmount(minAmount.subtract(BigDecimal.ONE));
        exception = assertThrows(IllegalArgumentException.class, () -> prescoringService.validateOffer(offer));
        assertEquals(correctMsg, exception.getMessage());


        correctMsg = "Total amount less than " + minAmount + " or bigger than " + maxAmount;
        offer.setTotalAmount(maxAmount.add(BigDecimal.ONE));
        exception = assertThrows(IllegalArgumentException.class, () -> prescoringService.validateOffer(offer));
        assertEquals(correctMsg, exception.getMessage());
        offer.setTotalAmount(minAmount.subtract(BigDecimal.ONE));
        exception = assertThrows(IllegalArgumentException.class, () -> prescoringService.validateOffer(offer));
        assertEquals(correctMsg, exception.getMessage());
    }
}