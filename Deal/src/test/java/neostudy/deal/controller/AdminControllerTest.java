package neostudy.deal.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import neostudy.deal.dto.ApplicationStatus;
import neostudy.deal.dto.ChangeType;
import neostudy.deal.entity.Application;
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

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
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
    void getApplications() throws Exception {
        Mockito.when(applicationService.getApplications()).thenReturn(new ArrayList<>());

        mockMvc.perform(MockMvcRequestBuilders.get("/deal/admin/application"))
                .andExpect(status().isOk());

    }
    @Test
    void getApplicationById() throws Exception {
        Mockito.when(applicationService.findApplicationById(1L)).thenThrow(new NotFoundException("Application 1 not found"));

        mockMvc.perform(MockMvcRequestBuilders.get("/deal/admin/application/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void setApplicationStatus() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/deal/admin/application/1")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("PREAPPROVAL")
                )
                .andExpect(status().isOk());
    }


    @Test
    void setApplicationStatusWhenApplicationDoesNotExists() throws Exception {
        Mockito.doThrow(new NotFoundException("Application 2 not found")).when(dealService).setAndSaveApplicationStatus(any(Long.class), any(ApplicationStatus.class), any(ChangeType.class));

        mockMvc.perform(MockMvcRequestBuilders.put("/deal/admin/application/2")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("PREAPPROVAL")
                )
                .andExpect(status().isNotFound());
    }
}