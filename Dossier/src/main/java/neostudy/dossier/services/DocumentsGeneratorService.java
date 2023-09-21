package neostudy.dossier.services;

import lombok.NonNull;
import neostudy.dossier.dto.ApplicationDTO;

import java.io.IOException;
import java.util.List;

public interface DocumentsGeneratorService {

    String generatePaymentScheduleDocument(@NonNull ApplicationDTO application) throws IOException;

    String generateCreditDocument(@NonNull ApplicationDTO application) throws IOException;

    String generateClientDataDocument(@NonNull ApplicationDTO application) throws IOException;

    List<String> generateAllDocuments(@NonNull ApplicationDTO application) throws IOException;
}
