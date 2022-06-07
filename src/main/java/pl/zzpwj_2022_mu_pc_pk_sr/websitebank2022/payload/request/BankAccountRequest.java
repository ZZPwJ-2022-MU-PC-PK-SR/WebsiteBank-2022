package pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.payload.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@Setter
public class BankAccountRequest {
    @NotBlank
    private Long typeId;
    @Pattern(regexp="^\\d{4}$")
    @NotBlank
    private String code;
}
