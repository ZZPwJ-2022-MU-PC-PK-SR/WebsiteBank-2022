package pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.models;


import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Entity
@Table(name = "bank_account_types")
public class BankAccountType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private double interest;
    @NotNull
    private double transfer_cost_min;
    @NotNull
    private double transfer_cost_interest;
    @NotNull
    @Size(max = 60)
    private String name;


    public BankAccountType() {
    }

    public BankAccountType(double interest, double transfer_cost_min, double transfer_cost_interest, String name) {
        this.interest = interest;
        this.transfer_cost_min = transfer_cost_min;
        this.transfer_cost_interest = transfer_cost_interest;
        this.name = name;
    }

    public double getInterest() {
        return interest;
    }

    public void setInterest(double interest) {
        this.interest = interest;
    }

    public double getTransfer_cost_min() {
        return transfer_cost_min;
    }

    public void setTransfer_cost_min(double transfer_cost_min) {
        this.transfer_cost_min = transfer_cost_min;
    }

    public double getTransfer_cost_interest() {
        return transfer_cost_interest;
    }

    public void setTransfer_cost_interest(double transfer_cost_interest) {
        this.transfer_cost_interest = transfer_cost_interest;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
