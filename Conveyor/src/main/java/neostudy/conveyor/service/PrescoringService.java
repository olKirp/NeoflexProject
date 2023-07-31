package neostudy.conveyor.service;

import neostudy.conveyor.dto.LoanApplicationRequestDTO;
import neostudy.conveyor.dto.LoanOfferDTO;

import java.math.BigDecimal;
import java.util.List;

public interface PrescoringService {
    List<LoanOfferDTO> createLoanOffers(LoanApplicationRequestDTO loanRequest);

    BigDecimal calculateRate(boolean isSalaryClient, boolean isInsurance, BigDecimal rate);

    boolean isAmountValid(BigDecimal amount);

    boolean isTermValid(Integer term);
}
