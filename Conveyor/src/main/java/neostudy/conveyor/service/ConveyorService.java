package neostudy.conveyor.service;

import neostudy.conveyor.dto.LoanApplicationRequestDTO;
import neostudy.conveyor.dto.LoanOfferDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.*;

import static neostudy.conveyor.service.ScoringService.*;

@Service
public class ConveyorService {

    Logger logger = LoggerFactory.getLogger(ConveyorService.class);

    PrescoringService prescoringService;

    ScoringService scoringService;

    @Autowired
    public ConveyorService(PrescoringService prescoringService, ScoringService scoringService) {
        this.prescoringService = prescoringService;
        this.scoringService = scoringService;
    }

    private static final Comparator<LoanOfferDTO> offersComparator = Comparator.comparing(LoanOfferDTO::getRate);

    public List<LoanOfferDTO> createLoanOffers(LoanApplicationRequestDTO loanRequest) {
        logger.info("Creating loan offers for request " + loanRequest + " started");

        List<LoanOfferDTO> offers = new ArrayList<>();
        if (!validateBusinessRules(loanRequest)) {
            logger.info("Loan request is not valid, empty array of offers will be return");
            return offers;
        }
        offers.add(prescoringService.createLoanOffer(loanRequest, true, true));
        offers.add(prescoringService.createLoanOffer(loanRequest, false, false));
        offers.add(prescoringService.createLoanOffer(loanRequest, false, true));
        offers.add(prescoringService.createLoanOffer(loanRequest, true, false));

        offers.sort(offersComparator);
        Collections.reverse(offers);

        logger.info("Loan offers was created");
        return offers;
    }

    private boolean validateBusinessRules(LoanApplicationRequestDTO loanRequest) {
        if (Period.between(loanRequest.getBirthdate(), LocalDate.now()).getYears() < 18) {
            logger.info("Loan request is not valid, user younger than 18");
            return false;
        }
        if (loanRequest.getAmount().compareTo(MIN_AMOUNT) < 0 || loanRequest.getAmount().compareTo(MAX_AMOUNT) > 0) {
            logger.info("Loan request is not valid, requested amount less than " + MIN_AMOUNT + " or bigger than " + MAX_AMOUNT);
            return false;
        }
        if (loanRequest.getTerm() < MIN_TERM || loanRequest.getTerm() > MAX_TERM) {
            logger.info("Loan request is not valid, term longer than " + MAX_TERM + " or shorter than " + MIN_TERM);
            return false;
        }
        logger.info("Loan request is valid");
        return true;
    }
}