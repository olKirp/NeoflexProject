package neostudy.deal.service;

import neostudy.deal.dto.FinishRegistrationRequestDTO;
import neostudy.deal.dto.LoanApplicationRequestDTO;
import neostudy.deal.entity.Client;

import java.util.Optional;

public interface ClientService {

    Client saveClient(Client client);

    Client createClientForLoanRequest(LoanApplicationRequestDTO loanRequest);

    Client mapFinishRegistrationRequestToClient(Client client, FinishRegistrationRequestDTO registrationRequest);

    Optional<Client> findClientByPassportSeriesAndPassportNumber(String passportSeries, String passportNumber);

    boolean existsClientByEmail(String email);

    Optional<Client> findClientByEmail(String email);
}