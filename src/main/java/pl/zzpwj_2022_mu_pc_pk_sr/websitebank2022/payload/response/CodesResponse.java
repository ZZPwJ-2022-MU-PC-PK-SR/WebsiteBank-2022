package pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
public class CodesResponse {
    private List<String> codes;
}
