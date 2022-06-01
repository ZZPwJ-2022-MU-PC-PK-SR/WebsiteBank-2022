package pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.models.EnumTransactionStatus;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.models.TransactionStatus;

import java.util.Optional;

public interface TransactionStatusRepository extends JpaRepository<TransactionStatus,Long> {
    Optional<TransactionStatus> findByName(EnumTransactionStatus name);
}
