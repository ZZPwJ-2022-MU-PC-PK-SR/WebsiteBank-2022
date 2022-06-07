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
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.models.*;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.payload.request.TransactionRequest;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.payload.response.MessageResponse;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.payload.response.TransactionBeginResponse;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.repository.*;
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

    public Double getRate(String currencyCode) {
        RestTemplate restTemplate = new RestTemplate();
        String tableAUrl = "http://api.nbp.pl/api/exchangerates/rates/A/" + currencyCode + "/?format=json";
        Double rate;
        try{
            ResponseEntity<Map> response = restTemplate.getForEntity(tableAUrl,Map.class);
            ArrayList<Map> rates = (ArrayList<Map>) response.getBody().get("rates");
            rate = (Double) rates.get(0).get("mid");
        } catch(HttpClientErrorException.NotFound ex) {
            String tableBUrl = "http://api.nbp.pl/api/exchangerates/rates/B/" + currencyCode + "/?format=json";
            try {
                ResponseEntity<Map> response = restTemplate.getForEntity(tableBUrl, Map.class);
                ArrayList<Map> rates = (ArrayList<Map>) response.getBody().get("rates");
                rate = (Double) rates.get(0).get("mid");
            } catch(HttpClientErrorException.NotFound ex2) {
                throw new RuntimeException("Error: currency not found");
            }
        }
        return rate;
    }

    @PostMapping("/transaction_begin")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> transactionBegin(@Valid @RequestBody TransactionRequest request, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        TransactionType transactionType = transactionTypeRepository.findByName(EnumTransactionType.valueOf(request.getType())).orElseThrow(() -> new RuntimeException("Error: Transaction type is not found"));
        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow(() -> new RuntimeException("Fatal authentication error"));
        BankAccount from = bankAccountRepository.findByAccountNumberAndUser(request.getFrom(),user).orElseThrow(() -> new RuntimeException("No account with that number and/or user found"));

        double amount = Double.parseDouble(request.getAmount().replace(',','.'));
        if (from.getMoney()-amount<0) {
            return ResponseEntity.badRequest().body(new TransactionBeginResponse("INVALID","Not enough cash to process transaction"));
        }
        if(request.getCurrencyCode()!=null && !request.getCurrencyCode().equals("PLN")) {
            Double rate = getRate(request.getCurrencyCode());
            amount *= rate;
            amount = Math.round(amount*100.0)/100.0;
        }

        Boolean isExternal = bankAccountRepository.existsBankAccountByAccountNumber(request.getTo());
        boolean enoughMoney = from.getMoney()-Double.parseDouble(request.getAmount())>=0;

        TransactionStatus status = transactionStatusRepository.findByName(enoughMoney? EnumTransactionStatus.PENDING:EnumTransactionStatus.REJECTED).orElseThrow(() -> new RuntimeException("Error: Transaction status not found"));
        if(enoughMoney) {
            from.setMoney(from.getMoney()-Double.parseDouble(request.getAmount()));
            bankAccountRepository.save(from);

        }

        Date date = new Date();
        Transaction transaction = new Transaction(transactionType,from,request.getTo(),isExternal,status,request.getTransferTitle(),amount,date);
        transactionRepository.save(transaction);

        return ResponseEntity.ok().body(new TransactionBeginResponse(status.toString(),"Transfer begin completed succesfully"));
        // TODO : check transaction types after adding other transaction types

        //Transaction transaction = new Transaction()
//        BankAccountType bankAccountType = bankAccountTypeRepository.findById(1L).get();
//        user.setRoles(roles);
//        userRepository.save(user);
//        user = userRepository.findByEmail(user.getEmail()).get();
//        String accountNumber = "";
//        for(int i = 0 ; i < 26; i++){
//            int rand = (new Random().nextInt(10));
//            accountNumber += String.valueOf(rand);
//
//        }
//        BankAccount bankAccount = new BankAccount(bankAccountType,user,1000, accountNumber);
//        bankAccountRepository.save(bankAccount);
//        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }
}
