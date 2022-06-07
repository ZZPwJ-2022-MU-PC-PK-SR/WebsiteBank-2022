package pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.payload.request;

import javax.validation.constraints.NotBlank;

public class AccountRequest {
    @NotBlank
    private String accountNumber;

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }
}
