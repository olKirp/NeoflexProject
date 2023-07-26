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
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static neostudy.conveyor.service.ScoringService.*;

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
        request.setAmount(MAX_AMOUNT.add(new BigDecimal(1)));
        List<LoanOfferDTO> offers = conveyorService.createLoanOffers(request);
        assertTrue(offers.isEmpty());
    }

    @Test
    void givenLessThanMinAmount_ThenReturnsEmptyList() {
        request.setAmount(MIN_AMOUNT.subtract(new BigDecimal(1)));
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
    @ValueSource(ints = {5, 61})
    void givenIncorrectTerm_ThenReturnsEmptyList(int term) {
        request.setTerm(term);
        List<LoanOfferDTO> offers = conveyorService.createLoanOffers(request);
        assertTrue(offers.isEmpty());
    }

    @Test
    void givenCorrectRequest_ThenReturnsList() {
        List<LoanOfferDTO> offers = conveyorService.createLoanOffers(request);

        assertEquals(4, offers.size());

        assertTrue(offers.get(0).getRate().compareTo(offers.get(1).getRate()) >= 0);
        assertTrue(offers.get(1).getRate().compareTo(offers.get(2).getRate()) >= 0);
        assertTrue(offers.get(2).getRate().compareTo(offers.get(3).getRate()) >= 0);
    }
}