package neostudy.dossier.services;

import neostudy.dossier.dto.enums.Theme;

public interface MailCreatorService {
    String createMailBody(Theme theme, Long appId);
}
