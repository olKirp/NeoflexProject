package neostudy.deal.service;

import neostudy.deal.dto.FinishRegistrationRequestDTO;
import neostudy.deal.dto.ScoringDataDTO;
import neostudy.deal.entity.Application;
import neostudy.deal.entity.Client;
import neostudy.deal.mapper.ScoringDataDTOMapper;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

class FinishRegistrationServiceImplTest {

    private static FinishRegistrationService finishRegistrationService;

    @BeforeAll
    static void init() {
        finishRegistrationService = new FinishRegistrationServiceImpl(Mappers.getMapper(ScoringDataDTOMapper.class));
    }

    @Test
    void mapToScoringData() {
        FinishRegistrationRequestDTO request = Instancio.create(FinishRegistrationRequestDTO.class);
        Client client = Instancio.create(Client.class);
        Application application = Instancio.create(Application.class);

        ScoringDataDTO res = finishRegistrationService.mapDTOsToScoringData(request, client, application);

        assertEquals(application.getAppliedOffer().getRequestedAmount(), res.getAmount());
        assertEquals(application.getAppliedOffer().getTerm(), res.getTerm());
        assertEquals(application.getAppliedOffer().getIsInsuranceEnabled(), res.getIsInsuranceEnabled());
        assertEquals(application.getAppliedOffer().getIsSalaryClient(), res.getIsSalaryClient());
        assertEquals(client.getFirstName(), res.getFirstName());
        assertEquals(client.getLastName(), res.getLastName());
        assertEquals(client.getMiddleName(), res.getMiddleName());
        assertEquals(client.getPassport().getNumber(), res.getPassportNumber());
        assertEquals(client.getPassport().getSeries(), res.getPassportSeries());
        assertEquals(request.getPassportIssueDate(), res.getPassportIssueDate());
        assertEquals(request.getPassportIssueBranch(), res.getPassportIssueBranch());
        assertEquals(request.getMaritalStatus(), res.getMaritalStatus());
        assertEquals(request.getDependentAmount(), res.getDependentAmount());
        assertEquals(request.getAccount(), res.getAccount());
    }

    @Test
    void invokeMethodsWithNull() {
        assertThrows(IllegalArgumentException.class, () -> finishRegistrationService.mapDTOsToScoringData(null, null,null));
    }

}