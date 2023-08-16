package neostudy.deal.service;

import lombok.RequiredArgsConstructor;
import neostudy.deal.dto.FinishRegistrationRequestDTO;
import neostudy.deal.dto.LoanApplicationRequestDTO;
import neostudy.deal.entity.Client;
import neostudy.deal.mapper.ClientMapper;
import neostudy.deal.repository.ClientRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

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
        Client existedClient = findClientByPassportSeriesAndPassportNumber(loanRequest.getPassportSeries(), loanRequest.getPassportNumber());
        modelMapper.map(loanRequest, existedClient);
        return existedClient;
    }

    public Client findClientByPassportSeriesAndPassportNumber(String passportSeries, String passportNumber) {
        return clientRepository.findClientByPassportSeriesAndPassportNumber(passportSeries, passportNumber);
    }

    public void addInfoToClient(Client client, FinishRegistrationRequestDTO registrationRequest) {
        clientMapper.updateClientFromFinishRegistrationRequest(registrationRequest, client);
    }

    public Client createClientForLoanRequest(LoanApplicationRequestDTO loanRequest) {
        return mapLoanRequestToClient(loanRequest);
    }
}
