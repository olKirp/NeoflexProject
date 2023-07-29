package neostudy.conveyor.service;

import neostudy.conveyor.dto.LoanApplicationRequestDTO;
import neostudy.conveyor.dto.LoanOfferDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import static neostudy.conveyor.service.ScoringService.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
public class PrescoringService {

    private final Logger logger = LoggerFactory.getLogger(PrescoringService.class);

    private static final Comparator<LoanOfferDTO> offersComparator = Comparator.comparing(LoanOfferDTO::getRate);

    public List<LoanOfferDTO> createLoanOffers(LoanApplicationRequestDTO loanRequest) {
        logger.info("Creating loan offers for request " + loanRequest + " started");

        List<LoanOfferDTO> offers = new ArrayList<>();
        if (!isAmountValid(loanRequest.getAmount()) ||
                !isTermValid(loanRequest.getTerm()) ||
                !isBirthdateValid(loanRequest.getBirthdate())) {
            logger.info("Loan request is not valid, empty array of offers will be return");
            return offers;
        }

        offers.add(createLoanOffer(loanRequest, true, true));
        offers.add(createLoanOffer(loanRequest, false, false));
        offers.add(createLoanOffer(loanRequest, false, true));
        offers.add(createLoanOffer(loanRequest, true, false));

        offers.sort(offersComparator);
        Collections.reverse(offers);

        logger.info("Loan offers was created");
        return offers;
    }

    public LoanOfferDTO createLoanOffer(LoanApplicationRequestDTO loanRequest, boolean isSalaryClient, boolean isInsurance) {
        LoanOfferDTO loanOffer = new LoanOfferDTO();

        BigDecimal totalRate = calculateRate(isSalaryClient, isInsurance, BASE_RATE);
        BigDecimal totalAmount = getTotalAmount(isInsurance, loanRequest.getAmount()) ;

        BigDecimal monthlyPayment = LoanCalculator.countMonthlyPayment(totalAmount.setScale(2, RoundingMode.HALF_UP), totalRate.setScale(2, RoundingMode.HALF_UP), loanRequest.getTerm());

        loanOffer.setRate(totalRate.setScale(2, RoundingMode.HALF_UP));
        loanOffer.setTotalAmount(totalAmount.setScale(2, RoundingMode.HALF_UP));
        loanOffer.setTerm(loanRequest.getTerm());
        loanOffer.setIsInsuranceEnabled(isInsurance);
        loanOffer.setIsSalaryClient(isSalaryClient);
        loanOffer.setRequestedAmount(loanRequest.getAmount());
        loanOffer.setMonthlyPayment(monthlyPayment);

        logger.info("Created loan offer: " + loanOffer);
        return loanOffer;
    }


    private BigDecimal getTotalAmount(boolean isInsurance, BigDecimal amount) {
        if (isInsurance) {
            return amount.add(getInsurancePrice(amount));
        } else {
            return amount;
        }
    }

    public BigDecimal calculateRate(boolean isSalaryClient, boolean isInsurance, BigDecimal rate) {
        if (isSalaryClient) {
            rate = rate.subtract(DISCOUNT_FOR_SALARY_CLIENT);
        }

        if (isInsurance) {
            rate = rate.subtract(DISCOUNT_FOR_INSURANCE);

        } else {
           rate = rate.add(PENALTY_FOR_NO_INSURANCE);
        }

        return rate;
    }

    public BigDecimal getInsurancePrice(BigDecimal amount) {
        return amount.multiply(new BigDecimal("0.3"));
    }

    public boolean isBirthdateValid(LocalDate birthdate) {
        if (Period.between(birthdate, LocalDate.now()).getYears() < 18) {
            logger.info("Loan request is not valid, user younger than 18");
            return false;
        }
        return true;
    }

    public boolean isAmountValid(BigDecimal amount) {
        if (amount.compareTo(MIN_AMOUNT) < 0 || amount.compareTo(MAX_AMOUNT) > 0) {
            logger.info("Loan request is not valid, requested amount less than " + MIN_AMOUNT + " or bigger than " + MAX_AMOUNT);
            return false;
        }
        return true;
    }

    public boolean isTermValid(Integer term) {
        if (term < MIN_TERM || term > MAX_TERM) {
            logger.info("Loan request is not valid, term longer than " + MAX_TERM + " or shorter than " + MIN_TERM);
            return false;
        }
        return true;
    }
}
