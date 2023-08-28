package neostudy.deal.repository;

import neostudy.deal.entity.Application;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApplicationRepository extends CrudRepository<Application, Long> {
    boolean existsApplicationByClientId(Long clientId);
    Application findApplicationByClientId(Long clientId);
}
