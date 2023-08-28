package neostudy.deal.service;

import neostudy.deal.dto.FinishRegistrationRequestDTO;
import neostudy.deal.dto.ScoringDataDTO;
import neostudy.deal.entity.Application;
import neostudy.deal.entity.Client;

public interface FinishRegistrationService {

    ScoringDataDTO mapToScoringData(FinishRegistrationRequestDTO request, Client client, Application application);
}
