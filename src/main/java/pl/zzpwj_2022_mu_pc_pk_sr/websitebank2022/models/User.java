package pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.models;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Entity
@Table(name = "users",uniqueConstraints = {
        @UniqueConstraint(columnNames = "username"),
        @UniqueConstraint(columnNames = "email")
})
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    @Size(max = 30)
    private String username;
    @NotBlank
    @Size(max = 60)
    @Email
    private String email;
    @NotBlank
    @Size(max = 120)
    private String password;
    @NotBlank
    @Size(max = 30)
    private String name;
    @NotBlank
    @Size(max = 40)
    private String surname;
    @NotBlank
    @Size(max = 120)
    private String personalId;
    @NotBlank
    @Size(max = 120)
    private String idCardNumber;
    @NotBlank
    @Size(max = 40)
    private String addressLiving;
    @NotBlank
    @Size(max = 40)
    private String addressCorrespondence;
    @Size(max = 40)
    private String boar;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable( name = "user_roles", joinColumns = @JoinColumn(name = "user_id"),inverseJoinColumns =  @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    @OneToMany(mappedBy = "user")
    private List<BankAccount> userAssoc;

    public User() {
    }

    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public User(String username, String email, String password, String name, String surname, String personal_id, String id_card_number, String address_living, String address_correspondence) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.name = name;
        this.surname = surname;
        this.personalId = personal_id;
        this.idCardNumber = id_card_number;
        this.addressLiving = address_living;
        this.addressCorrespondence = address_correspondence;
    }

}
