package antifraud.repository;

import antifraud.model.SuspiciousIP;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SuspiciousIPRepository extends CrudRepository<SuspiciousIP, Long> {

    @Override
    void delete(SuspiciousIP entity);

    void deleteSuspiciousIPByIp(String ip);

    List<SuspiciousIP> findAll();

    boolean existsSuspiciousIPByIp(String ip);

}
