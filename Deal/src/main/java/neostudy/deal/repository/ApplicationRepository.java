package neostudy.deal.repository;

import neostudy.deal.entity.Application;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends CrudRepository<Application, Long> {
    Optional<Application> findApplicationByClientId(Long clientId);
    List<Application> findAll();
}
