package pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
@Table(name = "passwrod_admins")
@Getter
@Setter
public class PasswordAdmin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    @Size(max = 150)
    private String password;

}
