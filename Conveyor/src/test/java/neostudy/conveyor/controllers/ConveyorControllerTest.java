package neostudy.conveyor.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import neostudy.conveyor.dto.LoanApplicationRequestDTO;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConveyorController.class)
@RunWith(MockitoJUnitRunner.class)
class ConveyorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createLoanOffers() throws Exception {
        LoanApplicationRequestDTO correctRequest = new LoanApplicationRequestDTO(new BigDecimal(100000), 10, "Name", "LastName", "MiddleName", "correct@mail.ru", LocalDate.of(1980, 10, 10), "1111", "222222");

        mockMvc.perform(MockMvcRequestBuilders.post("/conveyor/offers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(correctRequest)))
                .andExpect(status().isOk());
    }


    public String asJsonString(final Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}