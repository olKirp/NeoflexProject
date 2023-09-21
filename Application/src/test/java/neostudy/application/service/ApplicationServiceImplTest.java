package neostudy.application.service;

import neostudy.application.dto.LoanApplicationRequestDTO;
import neostudy.application.dto.LoanOfferDTO;
import neostudy.application.feignclient.DealAPIClient;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class ApplicationServiceImplTest {

    @Autowired
    private ApplicationServiceImpl applicationService;

    private static LoanApplicationRequestDTO request;

    private static LoanOfferDTO offer;

    @MockBean
    DealAPIClient client;

    @MockBean
    PrescoringService prescoringService;

    @BeforeAll
    static void init() {
        request = new LoanApplicationRequestDTO(new BigDecimal("100000.0"), 10, "Name", "Lastname", "Middlename", "correct@mail.ru", LocalDate.of(1980, 10, 10), "1111", "222222");
        offer = LoanOfferDTO.builder().term(10).requestedAmount(new BigDecimal("100000")).totalAmount(new BigDecimal("120000")).monthlyPayment(new BigDecimal("9000")).rate(new BigDecimal("5.00")).build();
    }


    @Test
    void createLoanOffers() {
        Mockito.when(client.createLoanOffers(request)).thenReturn(ResponseEntity.ok().body(new ArrayList<>()));

        ResponseEntity response = applicationService.createLoanOffers(request);
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
    }

    @Test
    void applyLoanOffer() {
        assertDoesNotThrow(() -> applicationService.applyLoanOffer(offer));
    }


    @Test
    void givenNull() {
        assertThrows(IllegalArgumentException.class, () -> applicationService.applyLoanOffer(null));
        assertThrows(IllegalArgumentException.class, () -> applicationService.createLoanOffers(null));
    }
}