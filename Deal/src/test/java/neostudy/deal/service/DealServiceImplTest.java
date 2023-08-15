package neostudy.deal.service;

import neostudy.deal.feignclient.ConveyorAPIClient;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DealServiceImplTest {

    private static DealServiceImpl dealService;

    @BeforeAll
    static void init(@Mock FinishRegistrationService finishRegistrationService,
    @Mock ApplicationService applicationService,
    @Mock ClientService clientService,
    @Mock CreditService creditService,
    @Mock ConveyorAPIClient conveyorAPIClient) {
        dealService = new DealServiceImpl(finishRegistrationService, applicationService, clientService, creditService, conveyorAPIClient);
    }

    @Test
    void getLoanOffers() {
    }

    @Test
    void approveLoanOffer() {
    }

    @Test
    void createCreditForApplication() {
    }

}