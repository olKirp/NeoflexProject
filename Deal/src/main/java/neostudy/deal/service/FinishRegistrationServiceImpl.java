package neostudy.deal.service;

import lombok.RequiredArgsConstructor;
import neostudy.deal.dto.FinishRegistrationRequestDTO;
import neostudy.deal.dto.ScoringDataDTO;
import neostudy.deal.entity.Application;
import neostudy.deal.entity.Client;
import neostudy.deal.mapper.ScoringDataDTOMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FinishRegistrationServiceImpl implements FinishRegistrationService {

    private final ScoringDataDTOMapper scoringDataDTOMapper;

    public ScoringDataDTO mapToScoringData(FinishRegistrationRequestDTO request, Client client, Application application) {
        return scoringDataDTOMapper.from(request, client, application);
    }

}
