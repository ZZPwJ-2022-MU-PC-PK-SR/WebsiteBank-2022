package pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.models.Transaction;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction,Long> {
    @Query(value = "SELECT t From Transaction as t inner join BankAccount as b ON t.from = b.id where  b.id = ?1")
    List<Transaction> findAllByToAccountNumber(Long id);
    Optional<Transaction> findFirstByOrderByIdAsc();
    @Query("select t from Transaction t where t.from.user.id = ?1 and t.amount between ?2 and ?3 and t.date between ?4 and ?5")
    List<Transaction> findByFilter(Long id,
                             Double greaterThanAmount,
                             Double lowerThanAmount,
                             Date greaterThanDate,
                             Date lowerThanDate,
                             Pageable pageable);
}
