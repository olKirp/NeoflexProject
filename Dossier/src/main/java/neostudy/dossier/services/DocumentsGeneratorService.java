package neostudy.dossier.services;

import neostudy.dossier.dto.ApplicationDTO;

import java.io.IOException;
import java.util.List;

public interface DocumentsGeneratorService {

    String generatePaymentScheduleDocument(ApplicationDTO application) throws IOException;

    String generateCreditDocument(ApplicationDTO application) throws IOException;

    String generateClientDataDocument(ApplicationDTO application) throws IOException;

    List<String> generateAllDocuments(ApplicationDTO application) throws IOException;
}
