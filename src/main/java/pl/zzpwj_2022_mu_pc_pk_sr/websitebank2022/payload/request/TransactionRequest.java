package pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.payload.request;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.annotations.EnumValidator;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.models.EnumTransactionType;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;


@Getter
@Setter
@AllArgsConstructor
public class TransactionRequest {
    @NotBlank
    @EnumValidator(
            enumClazz = EnumTransactionType.class,
            message = "Fatal warning: invalid transaction type"
    )
    private String type;
    @NotBlank
    @Size(min=26,max=28)
    private String from;
    @NotBlank
    @Size(min=26,max=28)
    private String to;
    @NotBlank
    @Size(max = 140)
    private String transferTitle;
    @NotBlank
    @Pattern(regexp="[1-9][0-9]{0,7}[,][0-9]{2}")
    private String amount;

}
