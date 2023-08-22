package neostudy.conveyor.service;

import lombok.RequiredArgsConstructor;
import neostudy.conveyor.dto.LoanApplicationRequestDTO;
import neostudy.conveyor.dto.LoanOfferDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanOffersServiceImpl implements LoanOffersService {

    private static final Comparator<LoanOfferDTO> offersComparator = Comparator.comparing(LoanOfferDTO::getRate);

    private final LoanCalculatorService loanCalculatorService;

    @Value("${constants.baseRate}")
    private BigDecimal baseRate;

    @Value("${constants.discountForInsurance}")
    private BigDecimal discountForInsurance;

    @Value("${constants.penaltyForNoInsurance}")
    private BigDecimal penaltyForNoInsurance;

    @Value("${constants.discountForSalaryClient}")
    private BigDecimal discountForSalaryClient;

    @Value("${constants.minimalRate}")
    private BigDecimal minimalRate;

    public List<LoanOfferDTO> createLoanOffers(LoanApplicationRequestDTO loanRequest) {
        List<LoanOfferDTO> offers = new ArrayList<>();
        offers.add(createLoanOffer(loanRequest, true, true));
        offers.add(createLoanOffer(loanRequest, false, false));
        offers.add(createLoanOffer(loanRequest, false, true));
        offers.add(createLoanOffer(loanRequest, true, false));

        offers.sort(offersComparator);
        Collections.reverse(offers);

        return offers;
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
}