package pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.security.WebSecurityConfig;

@SpringBootTest
@Import(WebSecurityConfig.class)
class WebsiteBank2022ApplicationTest {
    @Test
    public void loadContext(){

    }

}