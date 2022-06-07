package pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.models;


import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "cards")
public class Cards {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String bankAccountId;
    @Temporal(TemporalType.DATE)
    private Date accessDate;
    private String status;
    private String cardNumber;

    public Cards() {
    }

    public Cards(String bank_account_id, Date access_date, String status, String card_number) {
        this.bankAccountId = bank_account_id;
        this.accessDate = access_date;
        this.status = status;
        this.cardNumber = card_number;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBankAccountId() {
        return bankAccountId;
    }

    public void setBankAccountId(String bank_account_id) {
        this.bankAccountId = bank_account_id;
    }

    public Date getAccessDate() {
        return accessDate;
    }

    public void setAccessDate(Date access_date) {
        this.accessDate = access_date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String card_number) {
        this.cardNumber = card_number;
    }
}
