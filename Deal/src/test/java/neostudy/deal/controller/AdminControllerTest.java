package neostudy.deal.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import neostudy.deal.dto.ApplicationStatus;
import neostudy.deal.dto.enums.ChangeType;
import neostudy.deal.exceptions.NotFoundException;
import neostudy.deal.service.ApplicationService;
import neostudy.deal.service.DealService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminController.class)
class AdminControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    DealService dealService;

    @MockBean
    ApplicationService applicationService;

    @MockBean
    ModelMapper modelMapper;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void getApplicationById() throws Exception {
        Mockito.when(applicationService.getApplicationById(1L)).thenThrow(new NotFoundException("Application 1 not found"));

        mockMvc.perform(MockMvcRequestBuilders.get("/deal/admin/application/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void setApplicationStatus() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/deal/admin/application/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"PREAPPROVAL\"")
                )
                .andExpect(status().isOk());

        Mockito.doThrow(new NotFoundException("Application 1 not found")).when(dealService).setApplicationStatus(1L, ApplicationStatus.PREAPPROVAL, ChangeType.AUTOMATIC);

        mockMvc.perform(MockMvcRequestBuilders.put("/deal/admin/application/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"PREAPPROVAL\"")
                )
                .andExpect(status().isNotFound());
    }
}