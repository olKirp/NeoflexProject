package neostudy.deal.service;

import neostudy.deal.dto.FinishRegistrationRequestDTO;
import neostudy.deal.dto.LoanApplicationRequestDTO;
import neostudy.deal.entity.Client;

public interface ClientService {

    Client mapLoanRequestToClient(LoanApplicationRequestDTO loanRequest);

    Client saveClient(Client client);

    Client createClientForLoanRequest(LoanApplicationRequestDTO loanRequest);

    void addInfoToClient(Client client, FinishRegistrationRequestDTO registrationRequest);

    boolean isClientExistByINN(String inn);

    boolean isClientExistsByAccount(String account);
}