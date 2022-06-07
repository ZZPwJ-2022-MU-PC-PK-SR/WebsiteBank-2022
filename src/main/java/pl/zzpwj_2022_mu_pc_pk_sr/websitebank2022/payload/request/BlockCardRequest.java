package pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.payload.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BlockCardRequest {
    @NotBlank
    private Long cardID;

//    @Pattern(regexp="^\\d{4}$")
//    @NotBlank
//    private String code;
}
