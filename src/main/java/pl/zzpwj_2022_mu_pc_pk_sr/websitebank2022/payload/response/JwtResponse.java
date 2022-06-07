package pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.payload.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private Long id;
    private String username;
    private String email;
    private List<String> roles;


    public JwtResponse(String token){
        this.token = token;
    }

}
