package neostudy.conveyor.service;

import neostudy.conveyor.dto.LoanApplicationRequestDTO;
import neostudy.conveyor.dto.LoanOfferDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import static neostudy.conveyor.service.ScoringService.*;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class PrescoringService {

    ScoringService scoringService;

    Logger logger = LoggerFactory.getLogger(PrescoringService.class);

    @Autowired
    public PrescoringService(ScoringService scoringService) {
        this.scoringService = scoringService;
    }

    public LoanOfferDTO createLoanOffer(LoanApplicationRequestDTO loanRequest, boolean isSalaryClient, boolean isInsurance) {
        LoanOfferDTO loanOffer = new LoanOfferDTO();

        BigDecimal totalRate = getRate(isSalaryClient, isInsurance);
        BigDecimal totalAmount = getTotalAmount(isInsurance, loanRequest.getAmount()) ;

        BigDecimal monthlyPayment = scoringService.countMonthlyPayment(totalAmount.setScale(2, RoundingMode.HALF_UP), totalRate.setScale(2, RoundingMode.HALF_UP), loanRequest.getTerm());

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

    private BigDecimal getRate(boolean isSalaryClient, boolean isInsurance) {
        BigDecimal totalRate = new BigDecimal(BASE_RATE.toString());

        if (isSalaryClient) {
            totalRate = totalRate.subtract(DISCOUNT_FOR_SALARY_CLIENT);
        }

        if (isInsurance) {
            totalRate = totalRate.subtract(DISCOUNT_FOR_INSURANCE);

        } else {
            totalRate = totalRate.add(PENALTY_FOR_NO_INSURANCE);
        }

        return totalRate;
    }

    public BigDecimal getInsurancePrice(BigDecimal amount) {
        return amount.multiply(new BigDecimal("0.3"));
    }
}
