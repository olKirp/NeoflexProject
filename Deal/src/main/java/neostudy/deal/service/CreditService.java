package neostudy.deal.service;

import neostudy.deal.dto.CreditDTO;
import neostudy.deal.entity.Credit;

public interface CreditService {

    Credit createCreditFromCreditDTO(CreditDTO creditDTO);

    Credit saveCredit(Credit credit);
}
