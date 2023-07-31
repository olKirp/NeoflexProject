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

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PrescoringServiceImplTest {

    @Autowired
    PrescoringServiceImpl prescoringService;

    @Autowired
    Constants constants;

    private static LoanApplicationRequestDTO request;

    @BeforeEach
    void createLoanApplicationRequest() {
        request = new LoanApplicationRequestDTO(new BigDecimal("100000.0"), 10, "Name", "Lastname", "Middlename", "correct@mail.ru", LocalDate.of(1980, 10, 10), "1111", "222222");
    }

    @Test
    void givenIncorrectAmount_ThenReturnsEmptyList() {
        String correctMsg = "Requested amount less than " + constants.getMinAmount() + " or bigger than " + constants.getMaxAmount();

        request.setAmount(constants.getMaxAmount().add(BigDecimal.ONE));
        Exception exception = assertThrows(IllegalArgumentException.class, () -> prescoringService.createLoanOffers(request));
        assertEquals(correctMsg, exception.getMessage());

        request.setAmount(constants.getMinAmount().subtract(BigDecimal.ONE));
        exception = assertThrows(IllegalArgumentException.class, () -> prescoringService.createLoanOffers(request));
        assertEquals(correctMsg, exception.getMessage());
    }

    @Test
    void givenAgeLessThanEighteen_ThenReturnsEmptyList() {
        String correctMsg = "User younger than 18";

        request.setBirthdate(LocalDate.of(2010, 1, 1));
        Exception exception = assertThrows(IllegalArgumentException.class, () -> prescoringService.createLoanOffers(request));
        assertEquals(correctMsg, exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(ints = {5, 61})
    void givenIncorrectTerm_ThenReturnsEmptyList(int term) {
        String correctMsg = "Term longer than " + constants.getMaxTerm() + " or shorter than " + constants.getMinTerm();

        request.setTerm(term);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> prescoringService.createLoanOffers(request));
        assertEquals(correctMsg, exception.getMessage());
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
    void givenSalaryClientWithoutInsurance_ThenReturnsCorrectOffer() {
        boolean isSalaryClient = true;
        boolean isInsurance = false;
        BigDecimal correctRate = constants.getBaseRate().subtract(constants.getDiscountForSalaryClient()).add(constants.getPenaltyForNoInsurance()).setScale(2, RoundingMode.HALF_UP);
        BigDecimal correctAmount = request.getAmount().setScale(2, RoundingMode.HALF_UP);
        BigDecimal monthlyPayment = new BigDecimal("10487.54");


        List<LoanOfferDTO> offers = prescoringService.createLoanOffers(request);
        LoanOfferDTO offer = offers.get(1);

        assertEquals(correctRate, offer.getRate());
        assertEquals(correctAmount, offer.getTotalAmount());
        assertEquals(monthlyPayment, offer.getMonthlyPayment());

        assertEquals(request.getTerm(), offer.getTerm());
        assertEquals(request.getAmount(), offer.getRequestedAmount());
        assertEquals(isSalaryClient, offer.getIsSalaryClient());
        assertEquals(isInsurance, offer.getIsInsuranceEnabled());
    }

    @Test
    void givenSalaryClientWithInsurance_ThenReturnsCorrectOffer() {
        boolean isSalaryClient = true;
        boolean isInsurance = true;
        BigDecimal correctRate = constants.getBaseRate().subtract(constants.getDiscountForSalaryClient()).subtract(constants.getDiscountForInsurance()).setScale(2, RoundingMode.HALF_UP);
        BigDecimal correctAmount = new BigDecimal("130000.00");
        BigDecimal monthlyPayment = new BigDecimal("22034.67");

        request.setTerm(6);

        List<LoanOfferDTO> offers = prescoringService.createLoanOffers(request);
        LoanOfferDTO offer = offers.get(3);

        assertEquals(correctRate, offer.getRate());
        assertEquals(correctAmount, offer.getTotalAmount());
        assertEquals(monthlyPayment, offer.getMonthlyPayment());

        assertEquals(request.getTerm(), offer.getTerm());
        assertEquals(request.getAmount(), offer.getRequestedAmount());
        assertEquals(isSalaryClient, offer.getIsSalaryClient());
        assertEquals(isInsurance, offer.getIsInsuranceEnabled());
    }

    @Test
    void givenNoSalaryClientWithInsurance_ThenReturnsCorrectOffer() {
        boolean isSalaryClient = false;
        boolean isInsurance = true;
        BigDecimal correctRate = constants.getBaseRate().subtract(constants.getDiscountForInsurance()).setScale(2, RoundingMode.HALF_UP);
        BigDecimal correctAmount = new BigDecimal("130000.00");
        BigDecimal monthlyPayment = new BigDecimal("13499.65");

        List<LoanOfferDTO> offers = prescoringService.createLoanOffers(request);
        LoanOfferDTO offer = offers.get(2);

        assertEquals(correctRate, offer.getRate());
        assertEquals(correctAmount, offer.getTotalAmount());
        assertEquals(monthlyPayment, offer.getMonthlyPayment());

        assertEquals(request.getTerm(), offer.getTerm());
        assertEquals(request.getAmount(), offer.getRequestedAmount());
        assertEquals(isSalaryClient, offer.getIsSalaryClient());
        assertEquals(isInsurance, offer.getIsInsuranceEnabled());
    }

    @Test
    void givenNoSalaryClientWithoutInsurance_ThenReturnsCorrectOffer() {
        boolean isSalaryClient = false;
        boolean isInsurance = false;
        BigDecimal correctRate = constants.getBaseRate().add((constants.getPenaltyForNoInsurance())).setScale(2, RoundingMode.HALF_UP);
        BigDecimal correctAmount = request.getAmount().setScale(2, RoundingMode.HALF_UP);
        BigDecimal monthlyPayment = new BigDecimal("10605.46");

        List<LoanOfferDTO> offers = prescoringService.createLoanOffers(request);
        LoanOfferDTO offer = offers.get(0);

        assertEquals(correctRate, offer.getRate());
        assertEquals(correctAmount, offer.getTotalAmount());
        assertEquals(monthlyPayment, offer.getMonthlyPayment());

        assertEquals(request.getTerm(), offer.getTerm());
        assertEquals(request.getAmount(), offer.getRequestedAmount());
        assertEquals(isSalaryClient, offer.getIsSalaryClient());
        assertEquals(isInsurance, offer.getIsInsuranceEnabled());
    }
}