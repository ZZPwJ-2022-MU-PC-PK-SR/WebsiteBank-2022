package pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.models.AuthorizationCode;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.models.User;

import java.util.Set;

public interface AuthorizationCodeRepository extends JpaRepository<AuthorizationCode,Long> {
    Boolean existsByUserAndActiveIsTrue(User user);
    AuthorizationCode findTopByActiveTrueAndUserOrderByOrderNo(User user);
    Set<AuthorizationCode> findAuthorizationCodesByActiveTrueAndUser(User user);
}
