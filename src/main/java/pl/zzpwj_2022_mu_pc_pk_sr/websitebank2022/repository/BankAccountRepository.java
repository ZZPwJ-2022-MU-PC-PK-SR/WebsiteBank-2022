package pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.models.BankAccount;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.models.BankAccountType;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.models.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount,Long> {
    @Query("select b from BankAccount b where b.user.id = ?1")
    List<Optional<BankAccount>> findByUser_id(Long id);
    Optional<BankAccount> findByAccountNumberAndUser(String accountNumber, User user);
    Boolean existsBankAccountByAccountNumber(String accountNumber);

    @Query(value = "SELECT MAX(id) FROM bank_accounts",nativeQuery = true)
    Long getMaxId();


}
