package neostudy.deal.service;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import neostudy.deal.dto.FinishRegistrationRequestDTO;
import neostudy.deal.dto.ScoringDataDTO;
import neostudy.deal.entity.Application;
import neostudy.deal.entity.Client;
//import neostudy.deal.mapper.ScoringDataDTOMapper;
import neostudy.deal.exceptions.NotFoundException;
import neostudy.deal.mapper.ScoringDataDTOMapper;
import org.mapstruct.factory.Mappers;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class FinishRegistrationServiceImpl implements FinishRegistrationService {

    private final ScoringDataDTOMapper scoringDataDTOMapper;

    public ScoringDataDTO mapToScoringData(FinishRegistrationRequestDTO request, Client client, Application application) {
        return scoringDataDTOMapper.from(request, client, application);
    }

}
