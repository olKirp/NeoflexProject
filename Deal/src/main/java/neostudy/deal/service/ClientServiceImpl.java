package neostudy.deal.service;

import lombok.RequiredArgsConstructor;
import neostudy.deal.dto.FinishRegistrationRequestDTO;
import neostudy.deal.dto.LoanApplicationRequestDTO;
import neostudy.deal.entity.Client;
import neostudy.deal.mapper.ClientMapper;
import neostudy.deal.repository.ClientRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;

    private final ClientMapper clientMapper;

    private final ModelMapper modelMapper;

    public Client mapLoanRequestToClient(LoanApplicationRequestDTO loanRequest) {
        if (clientRepository.existsClientByPassportSeriesAndPassportNumber(loanRequest.getPassportSeries(), loanRequest.getPassportNumber())) {
            return updateClient(loanRequest);
        }
        return modelMapper.map(loanRequest, Client.class);
    }

    public Client saveClient(Client client) {
        return clientRepository.save(client);
    }

    private Client updateClient(LoanApplicationRequestDTO loanRequest) {
        Client existedClient = clientRepository.findClientByPassportSeriesAndPassportNumber(loanRequest.getPassportSeries(), loanRequest.getPassportNumber());
        modelMapper.map(loanRequest, existedClient);
        return existedClient;
    }

    public void addInfoToClient(Client client, FinishRegistrationRequestDTO registrationRequest) {
        clientMapper.updateClientFromFinishRegistrationRequest(registrationRequest, client);
    }

    public boolean isClientExistByINN(String inn) {
        return clientRepository.existsClientByEmploymentINN(inn);
    }

    public boolean isClientExistsByAccount(String account) {
        return clientRepository.existsClientByAccount(account);
    }

    public Client createClientForLoanRequest(LoanApplicationRequestDTO loanRequest) {
        Client client = mapLoanRequestToClient(loanRequest);
        return saveClient(client);
    }


}
