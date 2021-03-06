package pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.models.BankAccountType;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.models.User;

import java.util.Optional;

@Repository
public interface BankAccountTypeRepository extends JpaRepository<BankAccountType, Long> {
    Optional<BankAccountType> findById(Long id);
}
