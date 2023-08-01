package neostudy.conveyor.service;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Getter
@Component
public class Constants {
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
    @Value("${maxSalariesNum}")
    private BigDecimal maxSalariesNum;
    @Value("${insuranceCoefficient}")
    private BigDecimal insurance;
}