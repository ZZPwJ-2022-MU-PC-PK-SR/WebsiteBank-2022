package pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.payload.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Set;

@Getter
@Setter
public class SignupAdminRequest {
    @NotBlank
    @Size(min = 8, max = 30)
    private String username;
    @NotBlank
    @Size(max = 60)
    @Email
    private String email;

    private Set<String> role;

    @NotBlank
    @Size(min = 2, max = 40)
    private String password;

    @NotBlank
    @Size(min = 2, max = 40)
    private String name;

    @NotBlank
    @Size(min = 2, max = 40)
    private String surname;

    @NotBlank
    @Size(min = 2, max = 40)
    private String personalId;

    @NotBlank
    @Size(min = 2, max = 40)
    private String idCardNumber;

    @NotBlank
    @Size(min = 2, max = 40)
    private String addressLiving;

    @NotBlank
    @Size(min = 2, max = 40)
    private String addressCorrespondence;

    @NotBlank
    @Size(min = 20, max=50)
    private String passwordAdmin;
}
