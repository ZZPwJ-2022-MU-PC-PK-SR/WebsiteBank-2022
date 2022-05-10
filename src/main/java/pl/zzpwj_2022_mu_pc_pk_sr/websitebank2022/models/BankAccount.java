package pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.models;


import javax.persistence.*;

@Entity
@Table(name = "bank_accounts")
@IdClass(BankAccountTypeUserID.class)
public class BankAccount {

    @Id
    @ManyToOne
    @JoinColumn(name ="bank_account_types_id", referencedColumnName = "id")
    private BankAccountType bankAccountType;

    @Id
    @ManyToOne
    @JoinColumn (name ="user_id", referencedColumnName = "id")
    private User user;

    @Column(name = "money")
    private double money;


    public BankAccount(BankAccountType bankAccountType, User user, double money) {
        this.bankAccountType = bankAccountType;
        this.user = user;
        this.money = money;
    }

    public BankAccount() {

    }

    public BankAccountType getBankAccountType() {
        return bankAccountType;
    }

    public void setBankAccountType(BankAccountType bankAccountType) {
        this.bankAccountType = bankAccountType;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }
}
