package neostudy.deal.mapper;

import neostudy.deal.dto.FinishRegistrationRequestDTO;
import neostudy.deal.entity.Client;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper
public interface ClientMapper {

    @Mapping(source = "request.passportIssueDate", target = "client.passport.issueDate")
    @Mapping(source = "request.passportIssueBranch", target = "client.passport.issueBranch")
    @Mapping(source = "request.dependentAmount", target = "client.dependentAmount")
    @Mapping(source = "request.account", target = "client.account")
    @Mapping(source = "request.gender", target = "client.gender")
    @Mapping(source = "request.maritalStatus", target = "client.maritalStatus")
    @Mapping(source = "request.employmentDTO.status", target = "client.employment.status")
    @Mapping(source = "request.employmentDTO.employerINN", target = "client.employment.INN")
    Client updateClientFromFinishRegistrationRequest(FinishRegistrationRequestDTO request, @MappingTarget Client client);

}