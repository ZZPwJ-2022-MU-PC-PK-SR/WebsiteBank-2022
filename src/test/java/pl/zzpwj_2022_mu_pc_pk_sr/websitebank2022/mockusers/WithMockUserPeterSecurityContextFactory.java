package pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.mockusers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.models.AuthorizationCode;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.models.EnumRole;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.models.Role;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.models.User;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.repository.AuthorizationCodeRepository;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.repository.UserRepository;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.services.UserDetailsImpl;

import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashSet;

public class WithMockUserPeterSecurityContextFactory implements WithSecurityContextFactory<WithMockUserPeter> {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuthorizationCodeRepository authorizationCodeRepository;
    @Override
    public SecurityContext createSecurityContext(WithMockUserPeter annotation) {
        authorizationCodeRepository.deleteAll();
        userRepository.deleteAll();
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Role role = new Role();
        role.setId(1);
        role.setName(EnumRole.ROLE_USER);
        String password = "password";
        User user = new User("Alfonsy2","alfonsy@yourdomain.com","password",
                "Alfonsy", "Barabasz", "012345678922", "PPP123124",
                "testaddress 22", "testcorrespondence 33");
        userRepository.save(user);
        AuthorizationCode code = null;
        try {
            code = new AuthorizationCode(user,2);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        authorizationCodeRepository.save(code);
        user.setRoles(new HashSet<>(Arrays.asList(role)));
        UserDetailsImpl principal = UserDetailsImpl.build(user);
        Authentication auth = new UsernamePasswordAuthenticationToken(
                principal, null, principal.getAuthorities() );
        context.setAuthentication(auth);
        return context;
    }
}
