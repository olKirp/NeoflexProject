package neostudy.deal.service;

import neostudy.deal.dto.FinishRegistrationRequestDTO;
import neostudy.deal.dto.LoanApplicationRequestDTO;
import neostudy.deal.entity.Client;

public interface ClientService {

    Client saveClient(Client client);

    Client createClientForLoanRequest(LoanApplicationRequestDTO loanRequest);

    void addInfoToClient(Client client, FinishRegistrationRequestDTO registrationRequest);

    Client findClientByPassportSeriesAndPassportNumber(String passportSeries, String passportNumber);

}