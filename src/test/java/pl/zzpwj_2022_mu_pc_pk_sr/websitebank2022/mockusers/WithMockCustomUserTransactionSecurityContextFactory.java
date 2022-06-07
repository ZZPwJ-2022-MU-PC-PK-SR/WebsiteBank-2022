package pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.mockusers;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.models.EnumRole;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.models.Role;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.models.User;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.services.UserDetailsImpl;

import java.util.Arrays;
import java.util.HashSet;

public class WithMockCustomUserTransactionSecurityContextFactory
        implements WithSecurityContextFactory<WithMockCustomUserTransaction> {
    @Override
    public SecurityContext createSecurityContext(WithMockCustomUserTransaction annotation) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Role role = new Role();
        role.setId(1);
        role.setName(EnumRole.ROLE_USER);
        String password = "password";
        User user = new User("usernamefine","user@yourdomain.com",password);
        user.setId(1L);
        user.setRoles(new HashSet<>(Arrays.asList(role)));
        UserDetailsImpl principal = UserDetailsImpl.build(user);
        Authentication auth = new UsernamePasswordAuthenticationToken(
                principal, null, principal.getAuthorities() );
        context.setAuthentication(auth);
        return context;
    }

}
