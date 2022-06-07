package pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.controllers;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.models.Transaction;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.payload.response.MessageResponse;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.services.TransactionHistoryService;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.services.UserDetailsImpl;

import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@Controller
@RequestMapping("/api/transaction_history")
@Log4j2
public class TransactionHistoryController {

    @Autowired
    TransactionHistoryService transactionHistoryService;

    @GetMapping("/")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> getTransactionsHistory(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                    @RequestParam(defaultValue = "0")
                                                    @Size(max = 9) Double greaterThanAmount,
                                                    @RequestParam(defaultValue = "1e9")
                                                    @Size(max = 9) Double lowerThanAmount,
                                                    @RequestParam(required = false)
                                                    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date greaterThanDate,
                                                    @RequestParam(required = false)
                                                    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date lowerThanDate,
                                                    @RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "30") int size,
                                                    @RequestParam(defaultValue = "") List<String> sortList,
                                                    @RequestParam(defaultValue = "DESC") Sort.Direction sortOrder) {
        if (greaterThanAmount > lowerThanAmount) {
            return ResponseEntity.badRequest().body(new MessageResponse("greaterThanAmount can't be greater than lowerThanAmount"));
        }
        if (greaterThanDate != null && lowerThanDate != null && greaterThanDate.after(lowerThanDate)) {
            return ResponseEntity.badRequest().body(new MessageResponse("greaterThanDate can't be after lowerThanDate"));
        }
        List<Transaction> transactionsList = transactionHistoryService
                .getTransactionsHistory(userDetails.getId(),
                        greaterThanAmount,
                        lowerThanAmount,
                        greaterThanDate,
                        lowerThanDate,
                        page,
                        size,
                        sortList,
                        sortOrder.toString());
        return ResponseEntity.ok(transactionHistoryService.getTransactionHistoryStructure(transactionsList));
    }

}
