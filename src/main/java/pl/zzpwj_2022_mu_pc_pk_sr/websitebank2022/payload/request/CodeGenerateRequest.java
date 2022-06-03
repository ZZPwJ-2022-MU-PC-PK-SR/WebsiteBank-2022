package pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.payload.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class CodeGenerateRequest {
    @NotBlank
    private String username;
}
