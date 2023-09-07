package neostudy.deal.service;

import neostudy.deal.dto.*;
import neostudy.deal.dto.EmploymentPosition;
import neostudy.deal.entity.Client;
import neostudy.deal.entity.Employment;
import neostudy.deal.entity.Passport;
import neostudy.deal.exceptions.UniqueConstraintViolationException;
import neostudy.deal.mapper.ClientMapper;
import neostudy.deal.repository.ClientRepository;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class ClientServiceImplTest {

    static ClientServiceImpl clientService;

    static final Client existedClient = Instancio.create(Client.class);

    static ClientRepository clientRepository;

    @BeforeAll
    static void init() {
        clientRepository = Mockito.mock(ClientRepository.class);
        ModelMapper modelMapper = new ModelMapper();
        ClientMapper mapper = Mappers.getMapper(ClientMapper.class);
        existedClient.getPassport().setSeries("0000");
        Mockito.when(clientRepository.existsClientByPassportSeriesAndPassportNumber(existedClient.getPassport().getSeries(), existedClient.getPassport().getNumber())).thenReturn(true);

        Mockito.when(clientRepository.findClientByPassportSeriesAndPassportNumber(existedClient.getPassport().getSeries(), existedClient.getPassport().getNumber())).thenReturn(existedClient);

        clientService = new ClientServiceImpl(clientRepository, mapper, modelMapper);
    }

    @Test
    void createClientForLoanRequest() {
        LoanApplicationRequestDTO request = Instancio.create(LoanApplicationRequestDTO.class);
        request.setPassportNumber(existedClient.getPassport().getNumber());
        request.setPassportSeries(existedClient.getPassport().getSeries());

        Client client = clientService.createClientForLoanRequest(request);

        assertEquals(existedClient.getId(), client.getId());
        assertEquals(request.getBirthdate(), client.getBirthdate());
        assertEquals(request.getEmail(), client.getEmail());
        assertEquals(request.getFirstName(), client.getFirstName());
        assertEquals(request.getLastName(), client.getLastName());
        assertEquals(request.getMiddleName(), client.getMiddleName());

        request = Instancio.create(LoanApplicationRequestDTO.class);
        request.setPassportSeries("1111");
        client = clientService.createClientForLoanRequest(request);

        assertNull(client.getId());
        assertEquals(request.getBirthdate(), client.getBirthdate());
        assertEquals(request.getEmail(), client.getEmail());
        assertEquals(request.getFirstName(), client.getFirstName());
        assertEquals(request.getLastName(), client.getLastName());
        assertEquals(request.getMiddleName(), client.getMiddleName());
    }


    @Test
    void addInfoToClient() {
        EmploymentDTO employmentDTO = new EmploymentDTO();
        employmentDTO.setEmploymentPosition(EmploymentPosition.OWNER);
        employmentDTO.setStatus(EmploymentStatus.BUSINESS_OWNER);
        employmentDTO.setEmployerINN("11111111");
        employmentDTO.setSalary(new BigDecimal("1000000"));
        employmentDTO.setWorkExperienceCurrent(50);
        employmentDTO.setWorkExperienceTotal(100);

        FinishRegistrationRequestDTO request = new FinishRegistrationRequestDTO();
        request.setGender(Gender.MALE);
        request.setMaritalStatus(MaritalStatus.MARRIED);
        request.setAccount("1234567890");
        request.setDependentAmount(1);
        request.setPassportIssueBranch("Branch");
        request.setPassportIssueDate(LocalDate.now());
        request.setEmploymentDTO(employmentDTO);


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

        clientService.addInfoToClient(client, request);

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


        Mockito.when(clientRepository.existsClientByAccount(client.getAccount())).thenReturn(true);
        assertThrows(UniqueConstraintViolationException.class, () -> clientService.addInfoToClient(client, request));

        Mockito.when(clientRepository.existsClientByAccount(client.getAccount())).thenReturn(false);
        Mockito.when(clientRepository.existsClientByEmploymentINN(client.getEmployment().getINN())).thenReturn(true);
        assertThrows(UniqueConstraintViolationException.class, () -> clientService.addInfoToClient(client, request));
    }
}