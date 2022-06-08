package pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.models.BankAccount;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.models.BankAccountType;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.models.User;

@Getter
@Setter
public class BankAccountResponse {
    private Long id;
    private BankAccountType bankAccountType;
    private double money;
    private String accountNumber;

    public BankAccountResponse(BankAccount b) {
        this.id = b.getId();
        this.bankAccountType = b.getBankAccountType();
        this.money = b.getMoney();
        this.accountNumber = b.getAccountNumber();
    }
}
