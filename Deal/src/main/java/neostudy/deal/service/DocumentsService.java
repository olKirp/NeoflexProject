package neostudy.deal.service;

public interface DocumentsService {

    void sendDocuments(Long applicationId);

    void signDocumentsRequest(Long applicationId);

    void signDocuments(Long applicationId, String sesCode);
}
