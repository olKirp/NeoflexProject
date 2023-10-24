package neostudy.application.service;

import lombok.NonNull;
import neostudy.application.dto.LoanApplicationRequestDTO;
import neostudy.application.dto.LoanOfferDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;

@Service
public class PrescoringServiceImpl implements PrescoringService {

    @Value("${constants.minTerm}")
    private int minTerm;

    @Value("${constants.maxTerm}")
    private int maxTerm;

    @Value("${constants.minAmount}")
    private BigDecimal minAmount;

    @Value("${constants.maxAmount}")
    private BigDecimal maxAmount;

    public void validateLoanRequest(@NonNull LoanApplicationRequestDTO loanRequest) {
        Assert.notNull(loanRequest, "LoanApplicationRequestDTO is null");
        Assert.isTrue(isAmountValid(loanRequest.getAmount()), "Requested amount less than " + minAmount + " or bigger than " + maxAmount);
        Assert.isTrue(isTermValid(loanRequest.getTerm()), "Term longer than " + maxTerm + " or shorter than " + minTerm);
        Assert.isTrue(isBirthdateValid(loanRequest.getBirthdate()), "User younger than 18");
    }

    public void validateOffer(@NonNull LoanOfferDTO offer) {
        Assert.notNull(offer, "LoanApplicationRequestDTO is null");
        Assert.isTrue(isAmountValid(offer.getTotalAmount()), "Total amount less than " + minAmount + " or bigger than " + maxAmount);
        Assert.isTrue(isAmountValid(offer.getRequestedAmount()), "Requested amount less than " + minAmount + " or bigger than " + maxAmount);
        Assert.isTrue(isTermValid(offer.getTerm()), "Term longer than " + maxTerm + " or shorter than " + minTerm);
    }

    private boolean isBirthdateValid(LocalDate birthdate) {
        return Period.between(birthdate, LocalDate.now()).getYears() >= 18;
    }

    private boolean isAmountValid(BigDecimal amount) {
        return amount.compareTo(minAmount) >= 0 && amount.compareTo(maxAmount) <= 0;
    }

    private boolean isTermValid(Integer term) {
        return term >= minTerm && term <= maxTerm;
    }

}