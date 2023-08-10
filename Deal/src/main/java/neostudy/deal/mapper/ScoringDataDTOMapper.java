package neostudy.deal.mapper;

import neostudy.deal.dto.FinishRegistrationRequestDTO;
import neostudy.deal.dto.ScoringDataDTO;
import neostudy.deal.entity.Application;
import neostudy.deal.entity.Client;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface ScoringDataDTOMapper {
    @Mapping(source = "application.appliedOffer.requestedAmount", target = "amount")
    @Mapping(source = "application.appliedOffer.term", target = "term")
    @Mapping(source = "client.firstName", target = "firstName")
    @Mapping(source = "client.lastName", target = "lastName")
    @Mapping(source = "client.middleName", target = "middleName")
    @Mapping(source = "request.gender", target = "gender")
    @Mapping(source = "client.passport.series", target = "passportSeries")
    @Mapping(source = "client.passport.number", target = "passportNumber")
    @Mapping(source = "request.passportIssueDate", target = "passportIssueDate")
    @Mapping(source = "request.passportIssueBranch", target = "passportIssueBranch")
    @Mapping(source = "request.maritalStatus", target = "maritalStatus")
    @Mapping(source = "request.dependentAmount", target = "dependentAmount")
    @Mapping(source = "request.employmentDTO", target = "employment")
    @Mapping(source = "request.account", target = "account")
    @Mapping(source = "application.appliedOffer.isInsuranceEnabled", target = "isInsuranceEnabled")
    @Mapping(source = "application.appliedOffer.isSalaryClient", target = "isSalaryClient")
    ScoringDataDTO from(FinishRegistrationRequestDTO request, Client client, Application application);
}
