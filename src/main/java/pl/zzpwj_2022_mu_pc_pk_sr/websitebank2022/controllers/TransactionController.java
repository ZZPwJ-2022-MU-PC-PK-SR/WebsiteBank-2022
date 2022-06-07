package pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.controllers;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.aspectj.bridge.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.models.*;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.payload.request.TransactionRequest;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.payload.response.MessageResponse;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.payload.response.TransactionBeginResponse;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.repository.*;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.services.AuthorizeTransaction;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.services.CurrencyRates;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.services.UserDetailsImpl;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Set;

@CrossOrigin(origins = "*", maxAge = 3600)
@Controller
@RequestMapping("/api/transactions")
@Log4j2
@RequiredArgsConstructor
public class TransactionController {
    private final UserRepository userRepository;

    private final BankAccountRepository bankAccountRepository;

    private final TransactionRepository transactionRepository;

    private final TransactionTypeRepository transactionTypeRepository;
    private final TransactionStatusRepository transactionStatusRepository;
 
    private final CurrencyRates currencyRates;
    private final AuthorizeTransaction authorizeTransaction;

    @PostMapping("/transaction_begin")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> transaction(@Valid @RequestBody TransactionRequest request, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        TransactionType transactionType = transactionTypeRepository.findByName(EnumTransactionType.valueOf(request.getType())).orElseThrow(() -> new RuntimeException("Error: Transaction type is not found"));
        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow(() -> new RuntimeException("Fatal authentication error"));
        BankAccount from = bankAccountRepository.findByAccountNumberAndUser(request.getFrom(),user).orElseThrow(() -> new RuntimeException("No account with that number and/or user found"));
        EnumTransactionStatus status = EnumTransactionStatus.REJECTED;

        if(request.getFrom().equals(request.getTo())) {
            return ResponseEntity.badRequest().body(new TransactionBeginResponse(status.toString(),"Cannot send a transfer to same account"));
        }

        if(transactionType.getName()==EnumTransactionType.CARD) {
            request.setAuthorizationData(request.getAuthorizationData()+"#"+request.getFrom());
        }

        if(!authorizeTransaction.authorizeTransaction(transactionType,userDetails,request.getAuthorizationData())) {
            return ResponseEntity.badRequest().body(new TransactionBeginResponse(status.toString(),"Authorization failed"));
        }



        double amount = Double.parseDouble(request.getAmount().replace(',','.'));

        if(request.getCurrencyCode()!=null && !request.getCurrencyCode().equals("PLN")) {
            Double rate = currencyRates.getRate(request.getCurrencyCode());
            amount *= rate;
            amount = Math.round(amount*100.0)/100.0;
        }

        if (from.getMoney()-amount<0) {
            return ResponseEntity.badRequest().body(new TransactionBeginResponse(status.toString(),"Not enough cash to process transaction"));
        }

        Boolean isExternal = !bankAccountRepository.existsBankAccountByAccountNumber(request.getTo());
        boolean enoughMoney = from.getMoney()-amount>=0;

        if(enoughMoney) {
            from.setMoney(from.getMoney()-amount);
            bankAccountRepository.save(from);
            if(!isExternal) {
                BankAccount to = bankAccountRepository.findByAccountNumber(request.getTo()).get();
                to.setMoney(to.getMoney()+amount);
                bankAccountRepository.save(to);
            }
            status=EnumTransactionStatus.FINALIZED;
        }


        TransactionStatus dbStatus = transactionStatusRepository.findByName(status).get();
        Date date = new Date();
        Transaction transaction = new Transaction(transactionType,from,request.getTo(),isExternal,dbStatus,request.getTransferTitle(),amount,date);
        transactionRepository.save(transaction);

        return ResponseEntity.ok().body(new TransactionBeginResponse(status.toString(),"Transfer begin completed succesfully"));
    }

    @GetMapping("/get_dzik")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getDzik(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok().body(new MessageResponse(authorizeTransaction.getCurrentUserDzik(userDetails)));
    }
}
