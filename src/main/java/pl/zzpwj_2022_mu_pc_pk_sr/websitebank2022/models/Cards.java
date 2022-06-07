package pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.models;


import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Date;

@Entity
@Table(name = "cards")
public class Cards {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String bank_account_id;
    @Temporal(TemporalType.DATE)
    private Date access_date;
    private String status;
    private String card_number;

    public Cards() {
    }

    public Cards(String bank_account_id, Date access_date, String status, String card_number) {
        this.bank_account_id = bank_account_id;
        this.access_date = access_date;
        this.status = status;
        this.card_number = card_number;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBank_account_id() {
        return bank_account_id;
    }

    public void setBank_account_id(String bank_account_id) {
        this.bank_account_id = bank_account_id;
    }

    public Date getAccess_date() {
        return access_date;
    }

    public void setAccess_date(Date access_date) {
        this.access_date = access_date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCard_number() {
        return card_number;
    }

    public void setCard_number(String card_number) {
        this.card_number = card_number;
    }
}
