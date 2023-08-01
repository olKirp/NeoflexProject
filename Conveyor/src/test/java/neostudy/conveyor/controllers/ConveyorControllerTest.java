package neostudy.conveyor.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import neostudy.conveyor.dto.EmploymentDTO;
import neostudy.conveyor.dto.LoanApplicationRequestDTO;
import neostudy.conveyor.dto.ScoringDataDTO;
import neostudy.conveyor.dto.enums.Gender;
import neostudy.conveyor.dto.enums.MaritalStatus;
import neostudy.conveyor.service.PrescoringService;
import neostudy.conveyor.service.ScoringService;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConveyorController.class)
class ConveyorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ConveyorController conveyorController;

    @MockBean
    private PrescoringService prescoringService;

    @MockBean
    private ScoringService scoringService;

    @Autowired
    private ObjectMapper objectMapper;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(conveyorController)
                .setControllerAdvice(new ConveyorControllerAdvice())
                .build();
    }

    @Test
    void createLoanOffers() throws Exception {
        LoanApplicationRequestDTO correctRequest = new LoanApplicationRequestDTO(new BigDecimal(100000), 10, "Name", "LastName", "MiddleName", "correct@mail.ru", LocalDate.of(1980, 10, 10), "1111", "222222");

        mockMvc.perform(MockMvcRequestBuilders.post("/conveyor/offers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(correctRequest)))
                .andExpect(status().isOk());
    }

    @Test
    void createCredit() throws Exception {
        ScoringDataDTO scoringDataDTO = new ScoringDataDTO();
        EmploymentDTO employmentDTO = new EmploymentDTO();
        employmentDTO.setEmployerINN("123456789012");
        employmentDTO.setSalary(new BigDecimal("50000.00"));

        scoringDataDTO.setFirstName("Name");
        scoringDataDTO.setLastName("Lastname");
        scoringDataDTO.setMiddleName("Middlename");
        scoringDataDTO.setBirthdate(LocalDate.of(1980, 1, 1));
        scoringDataDTO.setIsInsuranceEnabled(true);
        scoringDataDTO.setIsSalaryClient(true);
        scoringDataDTO.setPassportNumber("111111");
        scoringDataDTO.setPassportSeries("1111");
        scoringDataDTO.setPassportIssueDate(LocalDate.of(2000, 1, 1));
        scoringDataDTO.setPassportIssueBranch("PassportIssueBranch");
        scoringDataDTO.setAccount("0123456789");
        scoringDataDTO.setEmployment(employmentDTO);
        scoringDataDTO.setAmount(new BigDecimal("100000.00"));
        scoringDataDTO.setTerm(6);
        scoringDataDTO.setGender(Gender.FEMALE);
        scoringDataDTO.setMaritalStatus(MaritalStatus.SINGLE);

        mockMvc.perform(MockMvcRequestBuilders.post("/conveyor/calculation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(scoringDataDTO)))
                .andExpect(status().isOk());
    }

    @Test
    void catchException_ThanReturnMessage() throws Exception {
        LoanApplicationRequestDTO correctRequest = new LoanApplicationRequestDTO(new BigDecimal(100000), 10, "Name", "LastName", "MiddleName", "correct@mail.ru", LocalDate.of(1980, 10, 10), "1111", "222222");

        when(prescoringService.createLoanOffers(correctRequest)).thenThrow(new IllegalArgumentException("Message"));
        mockMvc.perform(MockMvcRequestBuilders.post("/conveyor/offers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(correctRequest)))
                .andExpect(content().string(containsString("Message")));
    }

    public String asJsonString(final Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}