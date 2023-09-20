package neostudy.deal.repository;

import neostudy.deal.entity.Client;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientRepository extends CrudRepository<Client, Long> {

    Optional<Client> findClientByPassportSeriesAndPassportNumber(String passportSeries, String passportNumber);

    boolean existsClientByPassportSeriesAndPassportNumber(String passportSeries, String passportNumber);

    boolean existsClientByAccount(String account);

    boolean existsClientByEmploymentINN(String inn);

    boolean existsClientByEmail(String email);

    Optional<Client> findClientByEmail(String email);
}
