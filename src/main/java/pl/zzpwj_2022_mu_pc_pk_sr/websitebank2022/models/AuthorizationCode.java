package pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

@Entity
@Table(name = "authorization_codes")
@Getter
@Setter
@ToString
public class AuthorizationCode implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @NotNull
    @JoinColumn(name="user_id",referencedColumnName = "id")
    private User user;

    @Pattern(regexp="^\\d{4}$")
    @NotBlank
    private String code;

    @NotNull
    private boolean active;

    @NotNull
    private int orderNo;

    public AuthorizationCode(User user, int orderNo) throws NoSuchAlgorithmException {
        this.user=user;
        this.orderNo=orderNo;
        this.active=true;
        String allowedCharacters="0123456789";
        SecureRandom secureRandom = SecureRandom.getInstanceStrong();
        this.code= secureRandom.ints(4,0,allowedCharacters.length()).
                mapToObj(allowedCharacters::charAt).collect(StringBuilder::new, StringBuilder::append,StringBuilder::append).toString();
    }

    public AuthorizationCode() {

    }
}
