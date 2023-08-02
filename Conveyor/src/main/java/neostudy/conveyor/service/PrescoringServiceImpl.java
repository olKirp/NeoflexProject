package neostudy.conveyor.service;

import lombok.RequiredArgsConstructor;
import neostudy.conveyor.dto.LoanApplicationRequestDTO;
import neostudy.conveyor.dto.LoanOfferDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PrescoringServiceImpl implements PrescoringService {

    private static final Comparator<LoanOfferDTO> offersComparator = Comparator.comparing(LoanOfferDTO::getRate);

    private final LoanCalculatorService loanCalculatorService;

    @Value("${baseRate}")
    private BigDecimal baseRate;

    @Value("${minTerm}")
    private int minTerm;

    @Value("${maxTerm}")
    private int maxTerm;

    @Value("${minAmount}")
    private BigDecimal minAmount;

    @Value("${maxAmount}")
    private BigDecimal maxAmount;

    @Value("${discountForInsurance}")
    private BigDecimal discountForInsurance;

    @Value("${penaltyForNoInsurance}")
    private BigDecimal penaltyForNoInsurance;

    @Value("${discountForSalaryClient}")
    private BigDecimal discountForSalaryClient;

    @Value("${minimalRate}")
    private BigDecimal minimalRate;

    public List<LoanOfferDTO> createLoanOffers(LoanApplicationRequestDTO loanRequest) {

        validateLoanRequest(loanRequest);

        List<LoanOfferDTO> offers = new ArrayList<>();
        offers.add(createLoanOffer(loanRequest, true, true));
        offers.add(createLoanOffer(loanRequest, false, false));
        offers.add(createLoanOffer(loanRequest, false, true));
        offers.add(createLoanOffer(loanRequest, true, false));

        offers.sort(offersComparator);
        Collections.reverse(offers);

        return offers;
    }

    private void validateLoanRequest(LoanApplicationRequestDTO loanRequest) {
        Assert.notNull(loanRequest, "LoanApplicationRequestDTO is null");
        Assert.isTrue(isAmountValid(loanRequest.getAmount()), "Requested amount less than " + minAmount + " or bigger than " + maxAmount);
        Assert.isTrue(isTermValid(loanRequest.getTerm()), "Term longer than " + maxTerm + " or shorter than " + minTerm);
        Assert.isTrue(isBirthdateValid(loanRequest.getBirthdate()), "User younger than 18");
    }

    private LoanOfferDTO createLoanOffer(LoanApplicationRequestDTO loanRequest, boolean isSalaryClient, boolean isInsurance) {
        BigDecimal totalRate = calculateRate(isSalaryClient, isInsurance, baseRate);
        BigDecimal totalAmount = loanCalculatorService.calculateAmountWithInsurance(isInsurance, loanRequest.getAmount());
        BigDecimal monthlyPayment = loanCalculatorService.calculateMonthlyPayment(totalAmount.setScale(2, RoundingMode.HALF_UP), totalRate.setScale(2, RoundingMode.HALF_UP), loanRequest.getTerm());

        return LoanOfferDTO.builder()
                .applicationId(1L)
                .rate(totalRate.setScale(2, RoundingMode.HALF_UP))
                .totalAmount(totalAmount.setScale(2, RoundingMode.HALF_UP))
                .term(loanRequest.getTerm())
                .isInsuranceEnabled(isInsurance)
                .isSalaryClient(isSalaryClient)
                .requestedAmount(loanRequest.getAmount())
                .monthlyPayment(monthlyPayment)
                .build();
    }

    public BigDecimal calculateRate(boolean isSalaryClient, boolean isInsurance, BigDecimal rate) {
        if (isSalaryClient) {
            rate = rate.subtract(discountForSalaryClient);
        }

        if (isInsurance) {
            rate = rate.subtract(discountForInsurance);
        } else {
            rate = rate.add(penaltyForNoInsurance);
        }

        if (rate.compareTo(minimalRate) < 0) {
            return minimalRate;
        }

        return rate.setScale(2, RoundingMode.HALF_UP);
    }


    private boolean isBirthdateValid(LocalDate birthdate) {
        return Period.between(birthdate, LocalDate.now()).getYears() >= 18;
    }

    public boolean isAmountValid(BigDecimal amount) {
        return amount.compareTo(minAmount) >= 0 && amount.compareTo(maxAmount) <= 0;
    }

    public boolean isTermValid(Integer term) {
        return term >= minTerm && term <= maxTerm;
    }
}