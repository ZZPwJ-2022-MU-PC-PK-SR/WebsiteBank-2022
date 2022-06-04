package pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name = "transaction_types")
public class TransactionType implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    @Column(length = 15)
    private EnumTransactionType name;
    public TransactionType(){}

}
