package neostudy.deal.controller;

import neostudy.deal.exceptions.IncorrectApplicationStatusException;
import neostudy.deal.exceptions.IncorrectSesCodeException;
import neostudy.deal.exceptions.NotFoundException;
import neostudy.deal.service.DocumentsService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DocumentsController.class)
class DocumentsControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    DocumentsService documentsService;

    @Test
    void sendDocuments() throws Exception {
        Mockito.doThrow(new IncorrectApplicationStatusException("IncorrectApplicationStatusException")).when(documentsService).sendDocuments(1L);
        mockMvc.perform(MockMvcRequestBuilders.post("/1/send"))
                .andExpect(status().isConflict());

        Mockito.doThrow(new NotFoundException("NotFoundException")).when(documentsService).sendDocuments(2L);

        mockMvc.perform(MockMvcRequestBuilders.post("/2/send"))
                .andExpect(status().isNotFound());
    }

    @Test
    void signDocumentsRequest() throws Exception {
        Mockito.doThrow(new IncorrectApplicationStatusException("IncorrectApplicationStatusException")).when(documentsService).signDocumentsRequest(1L);

        mockMvc.perform(MockMvcRequestBuilders.post("/1/sign")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("1234"))
                .andExpect(status().isConflict());

        Mockito.doThrow(new NotFoundException("NotFoundExceptionn")).when(documentsService).signDocumentsRequest(2L);
        mockMvc.perform(MockMvcRequestBuilders.post("/2/sign")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("1234"))
                .andExpect(status().isNotFound());
    }

    @Test
    void signDocuments() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/1/code")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("12fv"))
                .andExpect(status().isBadRequest());

        Mockito.doThrow(new NotFoundException("Application 1 not found")).when(documentsService).signDocuments(1L, "1234");
        mockMvc.perform(MockMvcRequestBuilders.post("/1/code")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("1234"))
                .andExpect(status().isNotFound());

        Mockito.doThrow(new IncorrectSesCodeException("Incorrect ses-code")).when(documentsService).signDocuments(1L, "0234");
        mockMvc.perform(MockMvcRequestBuilders.post("/1/code")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("0234"))
                .andExpect(status().isConflict());
    }
}