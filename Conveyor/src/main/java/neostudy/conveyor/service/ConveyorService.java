package neostudy.conveyor.service;

import neostudy.conveyor.dto.LoanApplicationRequestDTO;
import neostudy.conveyor.dto.LoanOfferDTO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

@Service
public class ConveyorService {

    public static final BigDecimal MIN_AMOUNT = new BigDecimal(10000);
    public static final BigDecimal MAX_AMOUNT = new BigDecimal(7000000);

    public static final int MIN_TERM = 6;
    public static final int MAX_TERM = 60;

    public static final BigDecimal BASE_RATE = new BigDecimal(10);

    public static final BigDecimal DISCOUNT_FOR_SALARY_CLIENT = new BigDecimal("2.5");
    public static final BigDecimal DISCOUNT_FOR_INSURANCE = new BigDecimal("1.7");
    public static final BigDecimal PENALTY_FOR_NO_INSURANCE = new BigDecimal("3.0");

    public LoanOfferDTO createLoanOffer(LoanApplicationRequestDTO loanRequest, boolean isSalaryClient, boolean isInsurance) {
        LoanOfferDTO loanOffer = new LoanOfferDTO();

        BigDecimal totalRate = new BigDecimal(BASE_RATE.toString());
        BigDecimal totalAmount = loanRequest.getAmount();

        if (isSalaryClient) {
            totalRate = totalRate.subtract(DISCOUNT_FOR_SALARY_CLIENT);
        }

        if (isInsurance) {
            totalRate = totalRate.subtract(DISCOUNT_FOR_INSURANCE);
            totalAmount = totalAmount.add(getInsurancePrice(loanRequest.getAmount()));
        } else {
            totalRate = totalRate.add(PENALTY_FOR_NO_INSURANCE);
        }

        BigDecimal monthlyPayment = countMonthlyPayment(totalAmount.setScale(2, RoundingMode.HALF_UP), totalRate.setScale(2, RoundingMode.HALF_UP), loanRequest.getTerm());

        loanOffer.setRate(totalRate.setScale(2, RoundingMode.HALF_UP));
        loanOffer.setTotalAmount(totalAmount.setScale(2, RoundingMode.HALF_UP));
        loanOffer.setTerm(loanRequest.getTerm());
        loanOffer.setIsInsuranceEnabled(isInsurance);
        loanOffer.setIsSalaryClient(isSalaryClient);
        loanOffer.setRequestedAmount(loanRequest.getAmount());
        loanOffer.setMonthlyPayment(monthlyPayment);
        return loanOffer;
    }

    public List<LoanOfferDTO> createLoanOffers(LoanApplicationRequestDTO loanRequest) {
        List<LoanOfferDTO> offers = new ArrayList<>();
        if (!validateBusinessRules(loanRequest)) {
            return offers;
        }
        offers.add(createLoanOffer(loanRequest, true, true));
        offers.add(createLoanOffer(loanRequest, false, false));
        offers.add(createLoanOffer(loanRequest, false, true));
        offers.add(createLoanOffer(loanRequest, true, false));
        return offers;
    }

    public BigDecimal getInsurancePrice(BigDecimal amount) {
        return amount.multiply(new BigDecimal("0.3"));
    }

    private boolean validateBusinessRules(LoanApplicationRequestDTO loanRequest) {
        if (Period.between(loanRequest.getBirthdate(), LocalDate.now()).getYears() < 18) {
            return false;
        }

        if (loanRequest.getAmount().compareTo(MIN_AMOUNT) < 0 || loanRequest.getAmount().compareTo(MAX_AMOUNT) > 0) {
            return false;
        }

        if (loanRequest.getTerm() < 6 || loanRequest.getTerm() > 60) {
            return false;
        }

        return true;
    }

    public BigDecimal countMonthlyPayment(BigDecimal amount, BigDecimal rate, int term) {
        BigDecimal monthlyRate = rate.divide(new BigDecimal("1200.00"), 10, RoundingMode.HALF_UP);
        BigDecimal monthlyRateToThePowerOfTerm = monthlyRate.add(new BigDecimal("1.00")).pow(term).setScale(10, RoundingMode.HALF_UP);
        BigDecimal annuity = monthlyRate.multiply(monthlyRateToThePowerOfTerm).divide(monthlyRateToThePowerOfTerm.subtract(new BigDecimal("1.00")), 10, RoundingMode.HALF_UP);
        return annuity.multiply(amount).setScale(2, RoundingMode.HALF_UP);
    }
}