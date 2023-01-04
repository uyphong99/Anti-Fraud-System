package antifraud.repository;

import antifraud.model.StolenCard;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface StolenCardRepository extends CrudRepository<StolenCard, Long> {
    void deleteStolenCardByNumber(String number);

    List<StolenCard> findAll();

    boolean existsStolenCardByNumber(String number);
}
