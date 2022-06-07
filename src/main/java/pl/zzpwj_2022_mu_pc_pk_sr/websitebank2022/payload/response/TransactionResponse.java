package pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.payload.response;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.bytebuddy.build.Plugin;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.models.BankAccount;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.models.Transaction;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.models.TransactionStatus;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.models.TransactionType;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

@Getter
public class TransactionResponse {

    private final Long id;
    private final TransactionType type;
    private final String from;
    private final String toAccountNumber;
    private final Boolean isExternal;
    private final TransactionStatus status;
    private final String transferTitle;
    private final Double amount;
    private final String date;

    public TransactionResponse(Transaction t) {
        this.id = t.getId();
        this.type = t.getType();
        this.from = t.getFrom().getAccountNumber();
        this.toAccountNumber = t.getToAccountNumber();
        this.isExternal = t.getIsExternal();
        this.status = t.getStatus();
        this.transferTitle = t.getTransferTitle();
        this.amount = t.getAmount();
        this.date = new SimpleDateFormat("yyyy-MM-dd").format(t.getDate());
    }
}
