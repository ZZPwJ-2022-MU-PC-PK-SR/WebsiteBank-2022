package pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.models;

import org.hibernate.validator.constraints.Currency;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "bank_accounts")
@Getter
@Setter
public class BankAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name ="bank_account_types_id", referencedColumnName = "id")
    private BankAccountType bankAccountType;


    @ManyToOne
    @JoinColumn (name ="user_id", referencedColumnName = "id")
    private User user;

    @Column(name = "money")
    private double money;

    @Column(name = "account_number")
    private String accountNumber;


    public BankAccount(BankAccountType bankAccountType, User user, double money, String accountNumber) {
        this.bankAccountType = bankAccountType;
        this.user = user;
        this.money = money;
        this.accountNumber = accountNumber;
    }

    public BankAccount() {

    }

}
