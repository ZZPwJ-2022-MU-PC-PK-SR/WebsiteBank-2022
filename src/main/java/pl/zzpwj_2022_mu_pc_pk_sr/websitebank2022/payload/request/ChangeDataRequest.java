package pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.payload.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@Setter
public class ChangeDataRequest {
    @Size(max = 60)
    @Email
    private String email;

    @NotBlank
    @Size(min = 2, max = 40)
    private String password;

    @Size(min = 2, max = 40)
    private String name;

    @Size(min = 2, max = 40)
    private String surname;

    @Size(min = 2, max = 40)
    private String addressLiving;

    @Size(min = 2, max = 40)
    private String addressCorrespondence;

    @Size(min = 2, max = 40)
    private String newPassword;

    @Pattern(regexp="^\\d{4}$")
    @NotBlank
    private String code;
}
