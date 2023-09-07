package neostudy.deal.repository;

import neostudy.deal.entity.Client;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepository extends CrudRepository<Client, Long> {

    Client findClientByPassportSeriesAndPassportNumber(String passportSeries, String passportNumber);

    boolean existsClientByPassportSeriesAndPassportNumber(String passportSeries, String passportNumber);

    boolean existsClientByAccount(String account);

    boolean existsClientByEmploymentINN(String inn);

    boolean existsClientByEmail(String email);

    Client findClientByEmail(String email);
}
