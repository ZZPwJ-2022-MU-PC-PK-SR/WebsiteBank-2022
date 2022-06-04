package pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.models.Transaction;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction,Long> {
    @Query(value = "SELECT t From Transaction as t inner join BankAccount as b ON t.from = b.id where  b.id = ?1")
    List<Transaction> findAllByToAccountNumber(Long id);
}
