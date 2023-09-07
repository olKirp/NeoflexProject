package neostudy.dossier.services;

import lombok.Data;
import neostudy.dossier.dto.ApplicationDTO;
import neostudy.dossier.dto.ClientDTO;
import neostudy.dossier.dto.CreditDTO;
import neostudy.dossier.dto.PaymentScheduleElement;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
@Service
public class DocumentsGeneratorServiceImpl implements DocumentsGeneratorService {

    @Value("${documents.path}")
    private String pathToDocuments;

    @Override
    public String generatePaymentScheduleDocument(ApplicationDTO application) throws IOException {
        List<String> lines = new ArrayList<>();

        lines.add("Payment schedule for loan application " + application.getId() + ":");
        for (PaymentScheduleElement element : application.getCredit().getPaymentSchedule()) {
            lines.add(element.getNumber() + ".");
            lines.add("Date: " + element.getDate());
            lines.add("Total payment: " + element.getTotalPayment());
            lines.add("Interest payment " + element.getInterestPayment());
            lines.add("Debt payment " + element.getDebtPayment());
            lines.add("Remaining debt: " + element.getRemainingDebt());
        }
        String filename = pathToDocuments + "payment_schedule_" + application.getId() + ".txt";
        writeToFile(filename, lines);
        return filename;
    }

    @Override
    public String generateCreditDocument(ApplicationDTO application) throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add("Credit information for application " + application.getId() + ":");

        CreditDTO credit = application.getCredit();
        lines.add("Total amount: " + credit.getAmount());
        lines.add("Term: " + credit.getTerm());
        lines.add("Rate: " + credit.getRate());
        lines.add("Monthly payment: " + credit.getMonthlyPayment());
        lines.add("The full cost of credit: " + credit.getPsk());
        lines.add("Insurance enabled: " + credit.getIsInsuranceEnabled());
        lines.add("Salary client: " + credit.getIsSalaryClient());

        String filename = pathToDocuments + "credit_info_" + application.getId() + ".txt";
        writeToFile(filename, lines);
        return filename;
    }

    @Override
    public String generateClientDataDocument(ApplicationDTO application) throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add("Client information for application " + application.getId() + ":");

        ClientDTO client = application.getClient();
        lines.add("Client: " + client.getLastName() + " " + client.getFirstName() + " " + client.getMiddleName());
        lines.add("Birthdate: " + client.getBirthdate());
        lines.add("Gender: " + client.getGender());
        lines.add("Marital status: " + client.getMaritalStatus());
        lines.add("Dependents amount: " + client.getDependentAmount());
        lines.add("Account: " + client.getAccount());
        lines.add("Passport: " + client.getPassport().getSeries() + " " + client.getPassport().getNumber() + ", issued by: " + client.getPassport().getIssueBranch() + " " + client.getPassport().getIssueDate());
        lines.add("Employment: " + client.getEmployment());

        String filename = pathToDocuments + "client_info_" + application.getId() + ".txt";
        writeToFile(filename, lines);
        return filename;
    }

    @Override
    public List<String> generateAllDocuments(ApplicationDTO application) throws IOException {
        return Arrays.asList(generateClientDataDocument(application),
                generateCreditDocument(application),
                generatePaymentScheduleDocument(application));
    }

    private void writeToFile(String filename, List<String> lines) throws IOException {
        if (!Files.exists(Path.of(pathToDocuments))) {
            Files.createDirectory(Path.of(pathToDocuments));
        }
        Path file = Paths.get(filename);
        Files.write(file, lines, StandardCharsets.UTF_8);
    }

}
