package pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;
import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import org.apache.tomcat.util.json.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.models.*;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.payload.request.TransactionRequest;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.payload.response.MessageResponse;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.payload.response.TransactionResponse;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.repository.BankAccountRepository;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.repository.TransactionRepository;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.repository.TransactionStatusRepository;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.repository.TransactionTypeRepository;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.security.jwt.JwtUtils;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.services.TransactionHistoryService;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.services.UserDetailsImpl;

import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@Controller
@RequestMapping("/api/logged")
@Log4j2
public class PageController {
    @Autowired
    BankAccountRepository bankAccountRepository;
    @Autowired
    TransactionRepository transactionRepository;
    @Autowired
    TransactionTypeRepository transactionTypeRepository;
    @Autowired
    TransactionStatusRepository transactionStatusRepository;
    @Autowired
    TransactionHistoryService transactionHistoryService;
    @GetMapping("/user")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> userAccess(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(new MessageResponse(userDetails.getUsername()));
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> adminAccess() {
        return  ResponseEntity.ok(new MessageResponse("Admin content"));
    }

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> dashboard(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        List<BankAccount> bankAccounts = bankAccountRepository.findByUser_id(userDetails.getId()).stream().filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());
        String msg="";
        for (BankAccount bankAccount: bankAccounts) {
            msg+="Stan konta : "+bankAccount.getMoney()+"\n Oprocentowanie : "+bankAccount.getBankAccountType().getInterest()+"\n Typ rachunku :"+bankAccount.getBankAccountType().getName();
        }

        return ResponseEntity.ok(new MessageResponse(userDetails.getUsername()+"\n Imie : "+userDetails.getName()+"\n Nazwisko : "+userDetails.getSurname()
        +"\n Numer karty : "+userDetails.getIdCardNumber()+"\n"+msg));
    }

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

    @GetMapping("/get_history")
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
