package pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.models.Cards;

import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<Cards, Long> {
    Optional<Cards> findById(Long id);
}
