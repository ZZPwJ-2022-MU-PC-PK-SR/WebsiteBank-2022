package pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.mockusers;

import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockCustomUserTransactionSecurityContextFactory.class)
public @interface WithMockCustomUserTransaction {
    String username() default "user";
    String password() default "pwd";
    String firstName() default "Firstname";
    String lastName() default "Lastname";
}
