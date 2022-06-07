package pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.models.AuthorizationCode;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.models.PasswordAdmin;
@Repository
public interface AdminPasswordRepository extends JpaRepository<PasswordAdmin,Long> {

    Boolean existsPasswordAdminByPassword(String accountNumber);

}
