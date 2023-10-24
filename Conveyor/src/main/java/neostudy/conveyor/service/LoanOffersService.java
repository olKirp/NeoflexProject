package neostudy.conveyor.service;

import neostudy.conveyor.dto.LoanApplicationRequestDTO;
import neostudy.conveyor.dto.LoanOfferDTO;

import java.math.BigDecimal;
import java.util.List;

public interface LoanOffersService {
    List<LoanOfferDTO> createLoanOffers(LoanApplicationRequestDTO loanRequest);

    BigDecimal calculateRate(boolean isSalaryClient, boolean isInsurance, BigDecimal rate);
}