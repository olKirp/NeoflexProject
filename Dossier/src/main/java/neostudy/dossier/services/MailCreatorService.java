package neostudy.dossier.services;

import neostudy.dossier.dto.Theme;

public interface MailCreatorService {
    String createMailBody(Theme theme, Long appId);
}
