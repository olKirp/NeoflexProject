package neostudy.deal.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import neostudy.deal.dto.FinishRegistrationRequestDTO;
import neostudy.deal.dto.LoanApplicationRequestDTO;
import neostudy.deal.dto.LoanOfferDTO;
import neostudy.deal.exceptions.ApplicationAlreadyApprovedException;
import neostudy.deal.exceptions.CreditConveyorDeniedException;
import neostudy.deal.exceptions.NotFoundException;
import neostudy.deal.service.*;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DealController.class)
class DealControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DealService dealService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createLoanOffers() throws Exception {
        LoanApplicationRequestDTO correctRequest = new LoanApplicationRequestDTO(new BigDecimal(100000), 10, "Name", "LastName", "MiddleName", "correct@mail.ru", LocalDate.of(1980, 10, 10), "1111", "222222");

        mockMvc.perform(MockMvcRequestBuilders.post("/deal/application")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(correctRequest)))
                .andExpect(status().isOk());

        correctRequest.setPassportNumber("qqqqqq");
        mockMvc.perform(MockMvcRequestBuilders.post("/deal/application")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(correctRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void saveLoanOffer() throws Exception {
        LoanOfferDTO offer = new LoanOfferDTO();
        mockMvc.perform(MockMvcRequestBuilders.post("/deal/offer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(offer)))
                .andExpect(status().isBadRequest());

        offer.setApplicationId(1L);
        offer.setIsInsuranceEnabled(true);
        offer.setIsSalaryClient(true);
        offer.setRate(new BigDecimal("1.2"));
        offer.setRequestedAmount(new BigDecimal(100000));
        offer.setTotalAmount(new BigDecimal(120000));
        offer.setMonthlyPayment(new BigDecimal(12000));
        offer.setTerm(10);

        mockMvc.perform(MockMvcRequestBuilders.post("/deal/offer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(offer)))
                .andExpect(status().isOk());

        Mockito.doThrow(new NotFoundException("Application not found")).when(dealService).approveLoanOffer(offer);

        mockMvc.perform(MockMvcRequestBuilders.post("/deal/offer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(offer)))
                .andExpect(status().isNotFound());

        Mockito.doThrow(new ApplicationAlreadyApprovedException("Application already approved")).when(dealService).approveLoanOffer(offer);

        mockMvc.perform(MockMvcRequestBuilders.post("/deal/offer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(offer)))
                .andExpect(status().isConflict());


    }

    @Test
    void createCredit() throws Exception {
        FinishRegistrationRequestDTO registrationRequest = Instancio.create(FinishRegistrationRequestDTO.class);
        registrationRequest.setDependentAmount(-1);
        mockMvc.perform(MockMvcRequestBuilders.post("/deal/calculate/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationRequest)))
                .andExpect(status().isBadRequest());

        registrationRequest.setDependentAmount(0);
        registrationRequest.setAccount("0123456789");
        registrationRequest.setPassportIssueDate(LocalDate.now().minusMonths(1));
        registrationRequest.getEmploymentDTO().setEmployerINN("123456789098");
        mockMvc.perform(MockMvcRequestBuilders.post("/deal/calculate/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationRequest)))
                .andExpect(status().isOk());

        Mockito.doThrow(new ApplicationAlreadyApprovedException("Application already approved")).when(dealService).createCreditForApplication(registrationRequest, 1L);
        mockMvc.perform(MockMvcRequestBuilders.post("/deal/calculate/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationRequest)))
                .andExpect(status().isConflict());

        Mockito.doThrow(new CreditConveyorDeniedException("Credit conveyor exception")).when(dealService).createCreditForApplication(registrationRequest, 1L);
        mockMvc.perform(MockMvcRequestBuilders.post("/deal/calculate/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationRequest)))
                .andExpect(status().isOk());
    }
}