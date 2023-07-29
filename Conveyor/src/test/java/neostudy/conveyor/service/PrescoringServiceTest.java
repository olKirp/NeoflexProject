package neostudy.conveyor.service;

import neostudy.conveyor.dto.LoanApplicationRequestDTO;
import neostudy.conveyor.dto.LoanOfferDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

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
    void givenLargerThanMaxAmount_ThenReturnsEmptyList() {
        request.setAmount(MAX_AMOUNT.add(new BigDecimal(1)));
        List<LoanOfferDTO> offers = prescoringService.createLoanOffers(request);
        assertTrue(offers.isEmpty());
    }

    @Test
    void givenLessThanMinAmount_ThenReturnsEmptyList() {
        request.setAmount(MIN_AMOUNT.subtract(new BigDecimal(1)));
        List<LoanOfferDTO> offers = prescoringService.createLoanOffers(request);
        assertTrue(offers.isEmpty());
    }

    @Test
    void givenAgeLessThanEighteen_ThenReturnsEmptyList() {
        request.setBirthdate(LocalDate.now().minusYears(17));
        List<LoanOfferDTO> offers = prescoringService.createLoanOffers(request);
        assertTrue(offers.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(ints = {5, 61})
    void givenIncorrectTerm_ThenReturnsEmptyList(int term) {
        request.setTerm(term);
        List<LoanOfferDTO> offers = prescoringService.createLoanOffers(request);
        assertTrue(offers.isEmpty());
    }

    @Test
    void givenCorrectRequest_ThenReturnsCorrectList() {
        List<LoanOfferDTO> offers = prescoringService.createLoanOffers(request);

        assertEquals(4, offers.size());

        assertTrue(offers.get(0).getRate().compareTo(offers.get(1).getRate()) >= 0);
        assertTrue(offers.get(1).getRate().compareTo(offers.get(2).getRate()) >= 0);
        assertTrue(offers.get(2).getRate().compareTo(offers.get(3).getRate()) >= 0);
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