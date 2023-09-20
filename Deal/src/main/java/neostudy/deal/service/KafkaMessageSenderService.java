package neostudy.deal.service;

import neostudy.deal.dto.Theme;

public interface KafkaMessageSenderService {

    void sendMessage(String address, Theme theme, Long appId);

}
