package antifraud.repository;

import antifraud.model.Transaction;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;

public interface TransactionRepository extends CrudRepository<Transaction, Long> {
    List<Transaction> findTransactionsByDateBetweenAndNumber(Date date1, Date date2, String number);

    void deleteAll();

    Transaction findTransactionsById(Long id);

    List<Transaction> findAll();

    boolean existsById(Long id);
}
