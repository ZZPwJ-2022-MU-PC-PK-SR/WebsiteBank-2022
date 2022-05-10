package pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.models;


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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getPersonalId() {
        return personalId;
    }

    public void setPersonalId(String personalId) {
        this.personalId = personalId;
    }

    public String getIdCardNumber() {
        return idCardNumber;
    }

    public void setIdCardNumber(String id_card_number) {
        this.idCardNumber = id_card_number;
    }

    public String getAddressLiving() {
        return addressLiving;
    }

    public void setAddressLiving(String address_living) {
        this.addressLiving = address_living;
    }

    public String getAddressCorrespondence() {
        return addressCorrespondence;
    }

    public void setAddressCorrespondence(String address_correspondence) {
        this.addressCorrespondence = address_correspondence;
    }

    public String getBoar() {
        return boar;
    }

    public void setBoar(String boar) {
        this.boar = boar;
    }

    public List<BankAccount> getUserAssoc() {
        return userAssoc;
    }

    public void setUserAssoc(List<BankAccount> userAssoc) {
        this.userAssoc = userAssoc;
    }
}
