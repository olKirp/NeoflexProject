package neostudy.deal.service;

import lombok.NonNull;
import neostudy.deal.dto.FinishRegistrationRequestDTO;
import neostudy.deal.dto.ScoringDataDTO;
import neostudy.deal.entity.Application;
import neostudy.deal.entity.Client;

public interface FinishRegistrationService {

    ScoringDataDTO mapDTOsToScoringData(@NonNull FinishRegistrationRequestDTO request, @NonNull Client client, @NonNull Application application);
}
