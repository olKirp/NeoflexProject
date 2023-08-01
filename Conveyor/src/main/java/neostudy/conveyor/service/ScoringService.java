package neostudy.conveyor.service;

import neostudy.conveyor.dto.CreditDTO;
import neostudy.conveyor.dto.ScoringDataDTO;

public interface ScoringService {
    CreditDTO createCredit(ScoringDataDTO scoringData);
}