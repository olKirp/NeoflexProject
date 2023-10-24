package neostudy.deal.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import neostudy.deal.dto.FinishRegistrationRequestDTO;
import neostudy.deal.dto.LoanApplicationRequestDTO;
import neostudy.deal.entity.Client;
import neostudy.deal.exceptions.UniqueConstraintViolationException;
import neostudy.deal.mapper.ClientMapper;
import neostudy.deal.repository.ClientRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;

    private final ClientMapper clientMapper;

    private final ModelMapper modelMapper;

    public void saveClient(@NonNull Client client) {
        clientRepository.save(client);
    }

    private Client updateClient(LoanApplicationRequestDTO loanRequest, Client existedClient) {
        modelMapper.map(loanRequest, existedClient);
        return existedClient;
    }

    public Optional<Client> findClientByPassportSeriesAndPassportNumber(String passportSeries, String passportNumber) {
        return clientRepository.findClientByPassportSeriesAndPassportNumber(passportSeries, passportNumber);
    }

    public void mapFinishRegistrationRequestToClient(@NonNull Client client, @NonNull FinishRegistrationRequestDTO registrationRequest) {
        if (clientRepository.existsClientByAccount(registrationRequest.getAccount())) {
            throw new UniqueConstraintViolationException("Client with account " + registrationRequest.getAccount() + " already exists");
        } else if (clientRepository.existsClientByEmploymentINN(registrationRequest.getEmploymentDTO().getEmployerINN())) {
            throw new UniqueConstraintViolationException("Client with INN " + registrationRequest.getEmploymentDTO().getEmployerINN() + " already exists");
        }

        clientMapper.updateClientFromFinishRegistrationRequest(registrationRequest, client);
    }

    public Client createClientForLoanRequest(@NonNull LoanApplicationRequestDTO loanRequest) {
        return findClientByPassportSeriesAndPassportNumber(loanRequest.getPassportSeries(), loanRequest.getPassportNumber())
        .map(client -> updateClient(loanRequest, client))
                .orElse(modelMapper.map(loanRequest, Client.class));
    }

    public boolean existsClientByEmail(String email) {
        return clientRepository.existsClientByEmail(email);
    }

    public Optional<Client> findClientByEmail(String email) {
        return clientRepository.findClientByEmail(email);
    }
}
