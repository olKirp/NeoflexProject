package neostudy.deal.service;

import lombok.RequiredArgsConstructor;
import neostudy.deal.dto.CreditDTO;
import neostudy.deal.dto.enums.CreditStatus;
import neostudy.deal.entity.Credit;
import neostudy.deal.repository.CreditRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreditServiceImpl implements CreditService{

    private final CreditRepository creditRepository;

    private final ModelMapper modelMapper;

    public Credit createCreditFromCreditDTO(CreditDTO creditDTO) {
        Credit credit = modelMapper.map(creditDTO, Credit.class);
        credit.setCreditStatus(CreditStatus.CALCULATED);
        return credit;
    }

    public Credit saveCredit(Credit credit) {
        return creditRepository.save(credit);
    }
}
