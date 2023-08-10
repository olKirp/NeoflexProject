package neostudy.deal.mapper;

import neostudy.deal.dto.EmploymentDTO;
import neostudy.deal.dto.FinishRegistrationRequestDTO;
import neostudy.deal.dto.enums.EmploymentPosition;
import neostudy.deal.dto.enums.EmploymentStatus;
import neostudy.deal.dto.enums.Gender;
import neostudy.deal.dto.enums.MaritalStatus;
import neostudy.deal.entity.Client;
import neostudy.deal.entity.Employment;
import neostudy.deal.entity.Passport;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;


class ClientMapperTest {

    private final ClientMapper clientMapper = Mappers.getMapper(ClientMapper.class);

    @Test
    void updateClientFromFinishRegistrationRequest() {
        EmploymentDTO employmentDTO = new EmploymentDTO();
        employmentDTO.setEmploymentPosition(EmploymentPosition.OWNER);
        employmentDTO.setStatus(EmploymentStatus.BUSINESS_OWNER);
        employmentDTO.setEmployerINN("11111111");
        employmentDTO.setSalary(new BigDecimal("1000000"));
        employmentDTO.setWorkExperienceCurrent(50);
        employmentDTO.setWorkExperienceTotal(100);

        FinishRegistrationRequestDTO request = FinishRegistrationRequestDTO.builder()
                .gender(Gender.MALE)
                .maritalStatus(MaritalStatus.MARRIED)
                .account("1234567890")
                .dependentAmount(1)
                .passportIssueBranch("Branch")
                .passportIssueDate(LocalDate.now())
                .employmentDTO(employmentDTO)
                .build();

        Passport passport = new Passport();
        passport.setSeries("1111");
        passport.setNumber("222222");

        Client client = Client.builder()
                .firstName("Name")
                .middleName("Midname")
                .lastName("Lastname")
                .email("email@test.ru")
                .birthdate(LocalDate.of(1990, 11, 11))
                .passport(passport)
                .build();

        clientMapper.updateClientFromFinishRegistrationRequest(request, client);

        System.out.println(client);

        assertEquals(request.getGender(), client.getGender());
        assertEquals(request.getMaritalStatus(), client.getMaritalStatus());
        assertEquals(request.getAccount(), client.getAccount());
        assertEquals(request.getDependentAmount(), client.getDependentAmount());
        assertEquals(request.getPassportIssueBranch(), client.getPassport().getIssueBranch());
        assertEquals(request.getPassportIssueDate(), client.getPassport().getIssueDate());

        Employment employment = client.getEmployment();
        assertEquals(employmentDTO.getSalary(), employment.getSalary());
        assertEquals(employmentDTO.getWorkExperienceCurrent(), employment.getWorkExperienceCurrent());
        assertEquals(employmentDTO.getWorkExperienceTotal(), employment.getWorkExperienceTotal());

        assertEquals(employmentDTO.getStatus(), employment.getStatus());
        assertEquals(employmentDTO.getEmploymentPosition(), employment.getEmploymentPosition());
        assertEquals(employmentDTO.getEmployerINN(), employment.getINN());
    }
}