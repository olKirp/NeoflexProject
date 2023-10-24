package neostudy.deal.service;

import lombok.NonNull;
import neostudy.deal.dto.CreditDTO;
import neostudy.deal.entity.Credit;

public interface CreditService {

    Credit mapCreditDTOToCredit(@NonNull CreditDTO creditDTO);

    Credit saveCredit(@NonNull Credit credit);
}
