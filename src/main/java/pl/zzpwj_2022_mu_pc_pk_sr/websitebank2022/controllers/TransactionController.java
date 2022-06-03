package pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.controllers;


import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.models.*;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.payload.request.TransactionRequest;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.payload.response.MessageResponse;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.repository.BankAccountRepository;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.repository.TransactionRepository;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.repository.TransactionStatusRepository;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.repository.TransactionTypeRepository;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.services.UserDetailsImpl;

import javax.validation.Valid;
import java.util.Date;

@CrossOrigin(origins = "*", maxAge = 3600)
@Controller
@RequestMapping("/api/transactions")
@Log4j2
public class TransactionController {
    @Autowired
    BankAccountRepository bankAccountRepository;
    @Autowired
    TransactionRepository transactionRepository;
    @Autowired
    TransactionTypeRepository transactionTypeRepository;
    @Autowired
    TransactionStatusRepository transactionStatusRepository;

    @PostMapping("/transaction_begin")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> transactionBegin(@Valid @RequestBody TransactionRequest request, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        TransactionType transactionType = transactionTypeRepository.findByName(EnumTransactionType.valueOf(request.getType())).orElseThrow(() -> new RuntimeException("Error: Transaction type is not found"));
        BankAccount from = bankAccountRepository.findByAccountNumber(request.getFrom()).orElseThrow(() -> new RuntimeException("No account number with that account"));
        request.setAmount(request.getAmount().replace(',','.'));
        if (from.getMoney()-Double.parseDouble(request.getAmount())<0) {
            return ResponseEntity.badRequest().body(new MessageResponse("Not enough cash to process transaction"));
        }
        Boolean isExternal = bankAccountRepository.existsBankAccountByAccountNumber(request.getTo());
        System.out.println(isExternal);
        boolean enoughMoney = from.getMoney()-Double.parseDouble(request.getAmount())>=0;
        TransactionStatus status = transactionStatusRepository.findByName(enoughMoney? EnumTransactionStatus.PENDING:EnumTransactionStatus.REJECTED).orElseThrow(() -> new RuntimeException("Error: Transaction status not found"));
        Date date = new Date();
        Transaction transaction = new Transaction(transactionType,from,request.getTo(),isExternal,status,request.getTransferTitle(),Double.parseDouble(request.getAmount()),date);
        transactionRepository.save(transaction);
        if(enoughMoney) {
            from.setMoney(from.getMoney()-Double.parseDouble(request.getAmount()));
            bankAccountRepository.save(from);
        }
        return ResponseEntity.ok().body(new MessageResponse("Transfer started succesfully"));
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
