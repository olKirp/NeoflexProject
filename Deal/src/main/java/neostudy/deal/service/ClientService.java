package neostudy.deal.service;

import lombok.NonNull;
import neostudy.deal.dto.FinishRegistrationRequestDTO;
import neostudy.deal.dto.LoanApplicationRequestDTO;
import neostudy.deal.entity.Client;

import java.util.Optional;

public interface ClientService {
    void saveClient(@NonNull Client client);

    Client createClientForLoanRequest(@NonNull LoanApplicationRequestDTO loanRequest);

    void mapFinishRegistrationRequestToClient(@NonNull Client client, @NonNull FinishRegistrationRequestDTO registrationRequest);

    Optional<Client> findClientByPassportSeriesAndPassportNumber(String passportSeries, String passportNumber);

    boolean existsClientByEmail(String email);

    Optional<Client> findClientByEmail(String email);
}