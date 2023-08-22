package neostudy.application.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import feign.Request;
import feign.RequestTemplate;
import neostudy.application.dto.LoanApplicationRequestDTO;
import neostudy.application.dto.LoanOfferDTO;
import neostudy.application.exceptionshandler.ApplicationExceptionHandler;
import neostudy.application.feignclient.DealAPIClient;
import neostudy.application.service.ApplicationService;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ApplicationController.class)
class ApplicationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ApplicationController controller;

    @MockBean
    private ApplicationService applicationService;

    @MockBean
    DealAPIClient client;

    @Autowired
    private ObjectMapper objectMapper;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new ApplicationExceptionHandler())
                .build();
    }

    @Test
    void createLoanOffers() throws Exception {
        LoanApplicationRequestDTO correctRequest = new LoanApplicationRequestDTO(new BigDecimal(100000), 10, "Name", "LastName", "MiddleName", "correct@mail.ru", LocalDate.of(1980, 10, 10), "1111", "222222");

        mockMvc.perform(MockMvcRequestBuilders.post("/application")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(correctRequest)))
                .andExpect(status().isOk());
    }

    @Test
    void receiveIncorrectLoanOffers_thenReturnError() throws Exception {
        LoanApplicationRequestDTO loanRequest = new LoanApplicationRequestDTO(new BigDecimal(10), 10, "Name", "LastName", "MiddleName", "correct@mail.ru", LocalDate.now().plusMonths(1), "1111", "222222");

        mockMvc.perform(MockMvcRequestBuilders.post("/application")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(loanRequest)))
                .andExpect(status().isBadRequest());

        loanRequest.setBirthdate(LocalDate.now().minusYears(40));

        Mockito.doThrow(new IllegalArgumentException("Incorrect request")).when(applicationService).createLoanOffers(loanRequest);

        mockMvc.perform(MockMvcRequestBuilders.post("/application")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(loanRequest)))
                .andExpect(status().isBadRequest());

        Request request = Request.create(Request.HttpMethod.GET, "url",
                new HashMap<>(), null, new RequestTemplate());
        Mockito.doThrow(new FeignException.Conflict("msg", request, null, null)).when(applicationService).createLoanOffers(loanRequest);
        mockMvc.perform(MockMvcRequestBuilders.post("/application")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(loanRequest)))
                .andExpect(status().isConflict());

        request = Request.create(Request.HttpMethod.GET, "url",
                new HashMap<>(), null, new RequestTemplate());
        Mockito.doThrow(new FeignException.InternalServerError("msg", request, null, null)).when(applicationService).createLoanOffers(loanRequest);
        mockMvc.perform(MockMvcRequestBuilders.post("/application")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(loanRequest)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void saveLoanOffer() throws Exception {
        LoanOfferDTO offer = LoanOfferDTO
                .builder()
                .totalAmount(new BigDecimal("100000"))
                .requestedAmount(new BigDecimal("100000"))
                .rate(new BigDecimal("11"))
                .term(10)
                .monthlyPayment(new BigDecimal("10000"))
                .applicationId(1L)
                .isInsuranceEnabled(true)
                .isSalaryClient(true)
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/application/offer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(offer)))
                .andExpect(status().isOk());
    }

    @Test
    void saveIncorrectLoanOffer_thenReturnError() throws Exception {
        LoanOfferDTO offer = LoanOfferDTO
                .builder()
                .totalAmount(new BigDecimal("100000"))
                .requestedAmount(new BigDecimal("100000"))
                .rate(new BigDecimal("11"))
                .term(10)
                .monthlyPayment(new BigDecimal("10000"))
                .applicationId(1L)
                .isInsuranceEnabled(true)
                .isSalaryClient(true)
                .build();

        Mockito.doThrow(new IllegalArgumentException("Incorrect request")).when(applicationService).applyLoanOffer(offer);
        mockMvc.perform(MockMvcRequestBuilders.post("/application/offer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(offer)))
                .andExpect(status().isBadRequest());

        Request request = Request.create(Request.HttpMethod.GET, "url",
                new HashMap<>(), null, new RequestTemplate());
        Mockito.doThrow(new FeignException.Conflict("msg", request, null, null)).when(applicationService).applyLoanOffer(offer);
        mockMvc.perform(MockMvcRequestBuilders.post("/application/offer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(offer)))
                .andExpect(status().isConflict());
    }

    public String asJsonString(final Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}