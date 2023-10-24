package neostudy.deal.service;

import lombok.RequiredArgsConstructor;
import neostudy.deal.dto.EmailMessageDTO;
import neostudy.deal.dto.Theme;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class KafkaMessageSenderServiceImpl implements KafkaMessageSenderService {

    private final  KafkaTemplate<String, EmailMessageDTO> kafkaTemplate;

    private static final Map<Theme, String> themesToTopics = new HashMap<>();

    static {
        themesToTopics.put(Theme.SEND_DOCUMENTS, "send-documents");
        themesToTopics.put(Theme.FINISH_REGISTRATION, "finish-registration");
        themesToTopics.put(Theme.CREATE_DOCUMENTS, "create-documents");
        themesToTopics.put(Theme.SEND_SES, "send-ses");
        themesToTopics.put(Theme.CREDIT_ISSUED, "credit-issued");
        themesToTopics.put(Theme.APPLICATION_DENIED, "application-denied");
    }

    public void sendMessage(String address, Theme theme, Long appId) {
        kafkaTemplate.send(themesToTopics.get(theme), new EmailMessageDTO(address, appId, theme));
    }

}
