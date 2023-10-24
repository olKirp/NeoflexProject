package neostudy.dossier.services;

import neostudy.dossier.dto.ApplicationDTO;
import org.instancio.Instancio;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DocumentsGeneratorServiceImplTest {

    final static DocumentsGeneratorServiceImpl documentsGenerator = new DocumentsGeneratorServiceImpl();

    final static ApplicationDTO applicationDTO = Instancio.create(ApplicationDTO.class);

    final static String path = "./testDocuments/";

    final static String paymentScheduleName = path + "payment_schedule_1.txt";
    final static String creditName = path + "credit_info_1.txt";
    final static String clientName = path + "client_info_1.txt";


    @BeforeAll
    static void init() {
        documentsGenerator.setPathToDocuments(path);

        applicationDTO.setId(1L);
    }

    @Test
    void generatePaymentScheduleDocument() throws IOException {
        String documentName = documentsGenerator.generatePaymentScheduleDocument(applicationDTO);
        assertEquals(paymentScheduleName, documentName);
    }

    @Test
    void generateCreditDocument() throws IOException {
        String documentName = documentsGenerator.generateCreditDocument(applicationDTO);
        assertEquals(creditName, documentName);
    }

    @Test
    void generateClientDataDocument() throws IOException {
        String documentName = documentsGenerator.generateClientDataDocument(applicationDTO);
        assertEquals(clientName, documentName);
    }

    @Test
    void generateAllDocuments() throws IOException {
        List<String> list = documentsGenerator.generateAllDocuments(applicationDTO);
        assertEquals(3, list.size());

        assertEquals(paymentScheduleName, list.get(2));
        assertEquals(creditName, list.get(1));
        assertEquals(clientName, list.get(0));

    }

    @AfterAll
    public static void deleteFiles() {
        try {
            Files.deleteIfExists(Path.of("./testDocuments/payment_schedule_1.txt"));
            Files.deleteIfExists(Path.of("./testDocuments/credit_info_1.txt"));
            Files.deleteIfExists(Path.of("./testDocuments/client_info_1.txt"));
            Files.deleteIfExists(Path.of("./testDocuments"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}