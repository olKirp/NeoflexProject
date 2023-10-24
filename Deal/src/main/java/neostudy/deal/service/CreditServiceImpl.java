package neostudy.deal.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import neostudy.deal.dto.CreditDTO;
import neostudy.deal.dto.CreditStatus;
import neostudy.deal.entity.Credit;
import neostudy.deal.repository.CreditRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CreditServiceImpl implements CreditService{

    private final CreditRepository creditRepository;

    private final ModelMapper modelMapper;

    public Credit mapCreditDTOToCredit(@NonNull CreditDTO creditDTO) {
        Credit credit = modelMapper.map(creditDTO, Credit.class);
        credit.setCreditStatus(CreditStatus.CALCULATED);
        return credit;
    }

    public Credit saveCredit(@NonNull Credit credit) {
        return creditRepository.save(credit);
    }
}
