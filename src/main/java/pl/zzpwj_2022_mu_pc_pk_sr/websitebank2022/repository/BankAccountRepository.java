package pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.models.BankAccount;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.models.BankAccountType;

import java.util.List;
import java.util.Optional;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount,Long> {
    List<Optional<BankAccount>> findByUser_id(Long id);

}
