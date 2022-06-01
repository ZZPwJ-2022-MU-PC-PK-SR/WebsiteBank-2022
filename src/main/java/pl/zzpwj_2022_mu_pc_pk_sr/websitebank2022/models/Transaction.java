package pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.models;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;


@Entity
@Table(name = "transactions")
@Getter
@Setter
@NoArgsConstructor
public class Transaction implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "type_id",referencedColumnName = "id")
    private TransactionType type;

    @ManyToOne
    @NotNull
    @JoinColumn(name="bank_account_from_id",referencedColumnName = "id")
    private BankAccount from;

    @NotBlank
    private String toAccountNumber;
    @NotNull
    private Boolean isExternal;
    @ManyToOne
    @NotNull
    @JoinColumn(name = "status_id",referencedColumnName = "id")
    private TransactionStatus status;
    @NotBlank
    @Size(max = 140)
    private String transferTitle;
    @NotNull
    @Column(precision=10, scale=2)
    private Double amount;
    @NotNull
    @Temporal(TemporalType.DATE)
    private Date date;

    public Transaction(TransactionType type, BankAccount from, String to, Boolean isExternal, TransactionStatus status, String transferTitle, Double amount, Date date) {
        this.type = type;
        this.from = from;
        this.toAccountNumber = to;
        this.isExternal = isExternal;
        this.status = status;
        this.transferTitle = transferTitle;
        this.amount = amount;
        this.date = date;
    }
}