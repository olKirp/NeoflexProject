package neostudy.dossier.services;

import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.store.FolderException;
import com.icegreen.greenmail.util.ServerSetupTest;
import jakarta.activation.DataHandler;
import jakarta.annotation.Resource;
import jakarta.mail.BodyPart;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class MailSenderServiceImplTest {

    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP)
            .withConfiguration(GreenMailConfiguration.aConfig().withUser("duke", "springboot"))
            .withPerMethodLifecycle(false);

    @Resource
    MailSenderService sender;

    @BeforeAll
    public static void before() throws IOException {
        if (!Files.exists(Path.of("./testDocuments"))) {
            Files.createDirectory(Path.of("./testDocuments"));
        }
        Path file = Paths.get("./testDocuments/testFile.txt");
        Files.createFile(file);
    }

    @AfterAll
    public static void deleteFiles() throws IOException {
        Files.deleteIfExists(Path.of("./testDocuments/testFile.txt"));
    }

    @Test
    void sendEmail() throws MessagingException, IOException, FolderException {
        String subject = "Subject";
        String msg = "Message";
        String toAddress = "toAddress@localhost";

        sender.sendEmail(toAddress, subject,  msg);

        MimeMessage[] emails = greenMail.getReceivedMessages();
        assertEquals(subject, emails[0].getSubject());
        assertEquals(msg, emails[0].getContent());
        assertEquals(toAddress, emails[0].getAllRecipients()[0].toString());

        greenMail.purgeEmailFromAllMailboxes();
    }

    @Test
    void sendEmailWithAttachment() throws MessagingException, IOException, FolderException {
        String subject = "Subject";
        String msg = "Message";
        String toAddress = "toAddress@localhost";
        String filename = "testFile.txt";
        List<String> attachments = new ArrayList<>();
        attachments.add("./testDocuments/" + filename);

        sender.sendEmailWithAttachment(toAddress, subject, msg, attachments);

        MimeMessage[] emails = greenMail.getReceivedMessages();
        assertEquals(subject, emails[0].getSubject());
        assertEquals(toAddress, emails[0].getAllRecipients()[0].toString());

        String contentType = emails[0].getContentType();
        assertTrue(contentType.contains("multipart"));

        Multipart multipart = (Multipart) emails[0].getContent();

        BodyPart bodyPart = multipart.getBodyPart(1);
        DataHandler handler = bodyPart.getDataHandler();
        assertEquals(filename, handler.getName());

        greenMail.purgeEmailFromAllMailboxes();
    }
}
