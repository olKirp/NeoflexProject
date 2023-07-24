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
class ConveyorServiceTest {

    @Autowired
    ConveyorService conveyorService;

    private static LoanApplicationRequestDTO request;

    @BeforeEach
    void createLoanApplicationRequest() {
        request = new LoanApplicationRequestDTO(new BigDecimal("100000.0"), 10, "Name", "Lastname", "Middlename", "correct@mail.ru", LocalDate.of(1980, 10, 10), "1111", "222222");
    }

    @Test
    void givenLargerThanMaxAmount_ThenReturnsEmptyList() {
        request.setAmount(ConveyorService.MAX_AMOUNT.add(new BigDecimal(1)));
        List<LoanOfferDTO> offers = conveyorService.createLoanOffers(request);
        assertTrue(offers.isEmpty());
    }

    @Test
    void givenLessThanMinAmount_ThenReturnsEmptyList() {
        request.setAmount(ConveyorService.MIN_AMOUNT.subtract(new BigDecimal(1)));
        List<LoanOfferDTO> offers = conveyorService.createLoanOffers(request);
        assertTrue(offers.isEmpty());
    }

    @Test
    void givenAgeLessThanEighteen_ThenReturnsEmptyList() {
        request.setBirthdate(LocalDate.now().minusYears(17));
        List<LoanOfferDTO> offers = conveyorService.createLoanOffers(request);
        assertTrue(offers.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(ints = {ConveyorService.MIN_TERM - 1, ConveyorService.MAX_TERM + 1})
    void givenIncorrectTerm_ThenReturnsEmptyList(int term) {
        request.setTerm(term);
        List<LoanOfferDTO> offers = conveyorService.createLoanOffers(request);
        assertTrue(offers.isEmpty());
    }

    @Test
    void givenSalaryClientWithoutInsurance_whenCreateOneOffer_ThenReturnsCorrectOffer() {
        BigDecimal correctRate = ConveyorService.BASE_RATE.subtract(ConveyorService.DISCOUNT_FOR_SALARY_CLIENT).add(ConveyorService.PENALTY_FOR_NO_INSURANCE);
        BigDecimal correctAmount = request.getAmount();

        LoanOfferDTO offer = conveyorService.createLoanOffer(request, true, false);

        assertEquals(correctRate, offer.getRate());
        assertEquals(correctAmount, offer.getTotalAmount());
    }

    @Test
    void givenSalaryClientWithInsurance_whenCreateOneOffer_ThenReturnsCorrectOffer() {
        BigDecimal correctRate = ConveyorService.BASE_RATE.subtract(ConveyorService.DISCOUNT_FOR_SALARY_CLIENT).subtract(ConveyorService.DISCOUNT_FOR_INSURANCE).setScale(2, RoundingMode.HALF_UP);
        BigDecimal correctAmount = new BigDecimal("130000.00");
        BigDecimal monthlyPayment = new BigDecimal("22034.67");

        request.setTerm(6);
        request.setAmount(new BigDecimal("100000.00"));

        LoanOfferDTO offer = conveyorService.createLoanOffer(request, true, true);

        assertEquals(correctRate, offer.getRate());
        assertEquals(correctAmount, offer.getTotalAmount());
        assertEquals(request.getTerm(), offer.getTerm());
        assertEquals(request.getTerm(), offer.getTerm());
        assertEquals(request.getAmount(), offer.getRequestedAmount());
        assertEquals(monthlyPayment, offer.getMonthlyPayment());
    }

    @Test
    void givenNoSalaryClientWithInsurance_whenCreateOneOffer_ThenReturnsCorrectOffer() {
        BigDecimal correctRate = ConveyorService.BASE_RATE.subtract(ConveyorService.DISCOUNT_FOR_INSURANCE);
        BigDecimal correctAmount = new BigDecimal("130000.00");

        LoanOfferDTO offer = conveyorService.createLoanOffer(request, false, true);

        assertEquals(correctRate, offer.getRate());
        assertEquals(correctAmount, offer.getTotalAmount());
    }

    @Test
    void givenNoSalaryClientWithoutInsurance_whenCreateOneOffer_ThenReturnsCorrectOffer() {
        BigDecimal correctRate = ConveyorService.BASE_RATE.add((ConveyorService.PENALTY_FOR_NO_INSURANCE));
        BigDecimal correctAmount = request.getAmount();

        LoanOfferDTO offer = conveyorService.createLoanOffer(request, false, false);

        assertEquals(correctRate, offer.getRate());
        assertEquals(correctAmount, offer.getTotalAmount());
    }
}