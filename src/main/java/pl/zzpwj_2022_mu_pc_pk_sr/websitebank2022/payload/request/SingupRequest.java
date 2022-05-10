package pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.payload.request;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Set;

public class SingupRequest {
    @NotBlank
    @Size(min = 8, max = 30)
    private String username;
    @NotBlank
    @Size(max = 60)
    @Email
    private String email;

    private Set<String> role;

    @NotBlank
    @Size(min = 2, max = 40)
    private String password;

    @NotBlank
    @Size(min = 2, max = 40)
    private String name;

    @NotBlank
    @Size(min = 2, max = 40)
    private String surname;

    @NotBlank
    @Size(min = 2, max = 40)
    private String personalId;

    @NotBlank
    @Size(min = 2, max = 40)
    private String idCardNumber;

    @NotBlank
    @Size(min = 2, max = 40)
    private String addressLiving;

    @NotBlank
    @Size(min = 2, max = 40)
    private String addressCorrespondence;

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

    public Set<String> getRole() {
        return role;
    }

    public void setRole(Set<String> role) {
        this.role = role;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public void setIdCardNumber(String idCardNumber) {
        this.idCardNumber = idCardNumber;
    }

    public String getAddressLiving() {
        return addressLiving;
    }

    public void setAddressLiving(String addressLiving) {
        this.addressLiving = addressLiving;
    }

    public String getAddressCorrespondence() {
        return addressCorrespondence;
    }

    public void setAddressCorrespondence(String addressCorrespondence) {
        this.addressCorrespondence = addressCorrespondence;
    }
}
