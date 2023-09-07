package neostudy.deal.service;

import neostudy.deal.dto.CreditDTO;
import neostudy.deal.dto.enums.CreditStatus;
import neostudy.deal.entity.Credit;
import neostudy.deal.entity.PaymentScheduleElement;
import neostudy.deal.repository.CreditRepository;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CreditServiceImplTest {

    private static CreditServiceImpl creditService;

    private static final Credit credit = new Credit();

    @BeforeAll
    static void init(@Mock CreditRepository creditRepository) {
        Mockito.when(creditRepository.save(credit)).thenReturn(credit);
        creditService = new CreditServiceImpl(creditRepository, new ModelMapper());
    }

    @Test
    void createCreditFromCreditDTO() {
        CreditDTO creditDTO = Instancio.create(CreditDTO.class);
        ModelMapper mapper = new ModelMapper();
        List<PaymentScheduleElement> element = new ArrayList<>();
        for (int i = 0; i < creditDTO.getPaymentSchedule().size(); i++) {
            element.add(mapper.map(creditDTO.getPaymentSchedule().get(i), PaymentScheduleElement.class));
        }

        mapper.map(creditDTO.getPaymentSchedule(), element);


        Credit credit = creditService.createCreditFromCreditDTO(creditDTO);

        assertEquals(creditDTO.getAmount(), credit.getAmount());
        assertEquals(creditDTO.getTerm(), credit.getTerm());
        assertEquals(creditDTO.getIsInsuranceEnabled(), credit.getIsInsuranceEnabled());
        assertEquals(creditDTO.getIsSalaryClient(), credit.getIsSalaryClient());
        assertEquals(creditDTO.getPsk(), credit.getPsk());
        assertEquals(creditDTO.getRate(), credit.getRate());
        assertEquals(element, credit.getPaymentSchedule());
        assertEquals(creditDTO.getMonthlyPayment(), credit.getMonthlyPayment());
        assertEquals(CreditStatus.CALCULATED, credit.getCreditStatus());
    }

    @Test
    void saveCredit() {
        assertEquals(credit, creditService.saveCredit(credit));
    }
}