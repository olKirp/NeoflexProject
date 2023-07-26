package neostudy.conveyor.service;

import neostudy.conveyor.dto.LoanApplicationRequestDTO;
import neostudy.conveyor.dto.LoanOfferDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

import static neostudy.conveyor.service.ScoringService.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PrescoringServiceTest {

    @Autowired
    PrescoringService prescoringService;

    private static LoanApplicationRequestDTO request;

    @BeforeEach
    void createLoanApplicationRequest() {
        request = new LoanApplicationRequestDTO(new BigDecimal("100000.0"), 10, "Name", "Lastname", "Middlename", "correct@mail.ru", LocalDate.of(1980, 10, 10), "1111", "222222");
    }

    @Test
    void givenSalaryClientWithoutInsurance_whenCreateOneOffer_ThenReturnsCorrectOffer() {
        boolean isSalaryClient = true;
        boolean isInsurance = false;
        BigDecimal correctRate = BASE_RATE.subtract(DISCOUNT_FOR_SALARY_CLIENT).add(PENALTY_FOR_NO_INSURANCE).setScale(2, RoundingMode.HALF_UP);
        BigDecimal correctAmount = request.getAmount().setScale(2, RoundingMode.HALF_UP);
        BigDecimal monthlyPayment = new BigDecimal("10487.54");

        LoanOfferDTO offer = prescoringService.createLoanOffer(request, isSalaryClient, isInsurance);

        assertEquals(correctRate, offer.getRate());
        assertEquals(correctAmount, offer.getTotalAmount());
        assertEquals(monthlyPayment, offer.getMonthlyPayment());

        assertEquals(request.getTerm(), offer.getTerm());
        assertEquals(request.getAmount(), offer.getRequestedAmount());
        assertEquals(isSalaryClient, offer.getIsSalaryClient());
        assertEquals(isInsurance, offer.getIsInsuranceEnabled());

    }

    @Test
    void givenSalaryClientWithInsurance_whenCreateOneOffer_ThenReturnsCorrectOffer() {
        boolean isSalaryClient = true;
        boolean isInsurance = true;
        BigDecimal correctRate = BASE_RATE.subtract(DISCOUNT_FOR_SALARY_CLIENT).subtract(DISCOUNT_FOR_INSURANCE).setScale(2, RoundingMode.HALF_UP);
        BigDecimal correctAmount = new BigDecimal("130000.00");
        BigDecimal monthlyPayment = new BigDecimal("22034.67");

        request.setTerm(6);

        LoanOfferDTO offer = prescoringService.createLoanOffer(request, isSalaryClient, isInsurance);

        assertEquals(correctRate, offer.getRate());
        assertEquals(correctAmount, offer.getTotalAmount());
        assertEquals(monthlyPayment, offer.getMonthlyPayment());

        assertEquals(request.getTerm(), offer.getTerm());
        assertEquals(request.getAmount(), offer.getRequestedAmount());
        assertEquals(isSalaryClient, offer.getIsSalaryClient());
        assertEquals(isInsurance, offer.getIsInsuranceEnabled());
    }

    @Test
    void givenNoSalaryClientWithInsurance_whenCreateOneOffer_ThenReturnsCorrectOffer() {
        boolean isSalaryClient = false;
        boolean isInsurance = true;
        BigDecimal correctRate = BASE_RATE.subtract(DISCOUNT_FOR_INSURANCE).setScale(2, RoundingMode.HALF_UP);
        BigDecimal correctAmount = new BigDecimal("130000.00");
        BigDecimal monthlyPayment = new BigDecimal("13499.65");

        LoanOfferDTO offer = prescoringService.createLoanOffer(request, isSalaryClient, isInsurance);

        assertEquals(correctRate, offer.getRate());
        assertEquals(correctAmount, offer.getTotalAmount());
        assertEquals(monthlyPayment, offer.getMonthlyPayment());

        assertEquals(request.getTerm(), offer.getTerm());
        assertEquals(request.getAmount(), offer.getRequestedAmount());
        assertEquals(isSalaryClient, offer.getIsSalaryClient());
        assertEquals(isInsurance, offer.getIsInsuranceEnabled());
    }

    @Test
    void givenNoSalaryClientWithoutInsurance_whenCreateOneOffer_ThenReturnsCorrectOffer() {
        boolean isSalaryClient = false;
        boolean isInsurance = false;
        BigDecimal correctRate = BASE_RATE.add((PENALTY_FOR_NO_INSURANCE)).setScale(2, RoundingMode.HALF_UP);
        BigDecimal correctAmount = request.getAmount().setScale(2, RoundingMode.HALF_UP);
        BigDecimal monthlyPayment = new BigDecimal("10605.46");

        LoanOfferDTO offer = prescoringService.createLoanOffer(request, isSalaryClient, isInsurance);

        assertEquals(correctRate, offer.getRate());
        assertEquals(correctAmount, offer.getTotalAmount());
        assertEquals(monthlyPayment, offer.getMonthlyPayment());

        assertEquals(request.getTerm(), offer.getTerm());
        assertEquals(request.getAmount(), offer.getRequestedAmount());
        assertEquals(isSalaryClient, offer.getIsSalaryClient());
        assertEquals(isInsurance, offer.getIsInsuranceEnabled());
    }
}