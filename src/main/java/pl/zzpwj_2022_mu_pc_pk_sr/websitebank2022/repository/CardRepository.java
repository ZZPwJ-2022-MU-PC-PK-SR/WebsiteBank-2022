package pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.models.BankAccountType;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.models.Card;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.models.Role;

import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {
    Optional<Card> findById(Long id);
}
