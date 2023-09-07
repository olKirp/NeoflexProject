package neostudy.dossier.services;

import jakarta.mail.MessagingException;

import java.io.FileNotFoundException;
import java.util.List;

public interface MailSenderService {

    void sendEmail(String toAddress, String subject, String message);

    void sendEmailWithAttachment(String toAddress, String subject, String message, List<String> attachments) throws FileNotFoundException, MessagingException;
}
