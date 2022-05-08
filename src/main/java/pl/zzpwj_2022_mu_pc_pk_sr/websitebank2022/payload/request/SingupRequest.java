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
    @Size(min = 8, max = 40)
    private String password;

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
}