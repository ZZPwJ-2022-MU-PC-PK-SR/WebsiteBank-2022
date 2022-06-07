package pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.models.*;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.payload.request.AccountRequest;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.payload.request.BlockCardRequest;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.payload.request.RequestNewCardRequest;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.payload.response.MessageResponse;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.repository.BankAccountRepository;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.repository.TransactionRepository;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.repository.TransactionStatusRepository;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.repository.TransactionTypeRepository;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.repository.*;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.services.CheckCode;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.services.UserDetailsImpl;

import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@Controller
@RequestMapping("/api/logged")
@Log4j2
@RequiredArgsConstructor
public class PageController {
    private final CheckCode checkCode;
    private final CardRepository cardRepository;
    private final PasswordEncoder encoder;
  
    private final BankAccountTypeRepository bankAccountTypeRepository;
    private final BankAccountRepository bankAccountRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionTypeRepository transactionTypeRepository;
    private final TransactionStatusRepository transactionStatusRepository;
  
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
        ArrayList<Map<String,Object>> list = new ArrayList<>();
        for (BankAccount bankAccount: bankAccounts) {
            Map<String, Object> rtn = new LinkedHashMap<>();
            rtn.put("Stan konta",bankAccount.getMoney());
            rtn.put("Nr konta",bankAccount.getAccountNumber());
            rtn.put("Oprocentowanie",bankAccount.getBankAccountType().getInterest());
            rtn.put("Typ rachunku",bankAccount.getBankAccountType().getName());
            rtn.put("Imie",userDetails.getName());
            rtn.put("Nazwisko",userDetails.getName());
            list.add(rtn);
            msg+="Stan konta : "+bankAccount.getMoney()+"\n Oprocentowanie : "+bankAccount.getBankAccountType().getInterest()+"\n Typ rachunku :"+bankAccount.getBankAccountType().getName();
        }
        return ResponseEntity.ok(list);
    }
    @GetMapping("/account_percentage_money")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> account_percentage_money(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody AccountRequest accountRequest) {
        BankAccount bankAccount = bankAccountRepository.findByAccountNumber(accountRequest.getAccountNumber()).orElseThrow(() -> new RuntimeException("No account number with that account"));
        bankAccount.setMoney((bankAccount.getMoney()*bankAccount.getBankAccountType().getInterest())+ bankAccount.getMoney());
        bankAccountRepository.save(bankAccount);
        return ResponseEntity.ok("Operation finished correctly");
    }

    @GetMapping("/block_the_card")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> block_the_card(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody BlockCardRequest blockCardRequest) {
//        if(!checkCode.checkCode(blockCardRequest.getCode(),userDetails)){
//            return ResponseEntity.ok("Operation finished inCorrectly code is incorrect");
//        }
        List<BankAccount> bankAccounts = bankAccountRepository.findByUser_id(userDetails.getId()).stream().filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());
        Cards card = cardRepository.findById(blockCardRequest.getCardID()).orElseThrow(() -> new RuntimeException("No card with that card id"));
        for (BankAccount bankAccount: bankAccounts) {
            if(bankAccount.getAccountNumber().equals(card.getBank_account_id())){
                if(card.getStatus().equals("inActive")){
                    card.setStatus("Active");
                }else {
                    card.setStatus("inActive");
                }

                cardRepository.save(card);
                return ResponseEntity.ok("Operation finished correctly");
            }
        }
        return ResponseEntity.ok("Operation finished inCorrectly");
    }
    @GetMapping("/request_new_card")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> request_new_card(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody RequestNewCardRequest requestNewCardRequest) {
        if(!checkCode.checkCode(requestNewCardRequest.getCode(),userDetails)){
            return ResponseEntity.ok("Operation finished inCorrectly code is incorrect");
        }
        String cardNUmber = "";
        for(int i = 0 ; i < 12; i++){
            int rand = (new Random().nextInt(10));
            cardNUmber += String.valueOf(rand);
        }
        Date date = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.YEAR,2);
        Date newDate = c.getTime();
        List<BankAccount> bankAccounts = bankAccountRepository.findByUser_id(userDetails.getId()).stream().filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());
        for (BankAccount bankAccount: bankAccounts) {
            if(bankAccount.getUser().getId().equals(userDetails.getId())){
                Cards card = new Cards(bankAccount.getAccountNumber(),newDate,"active",encoder.encode(cardNUmber));
                cardRepository.save(card);
                return ResponseEntity.ok("Operation finished Correctly");
            }
        }
        return (ResponseEntity<?>) ResponseEntity.badRequest();
    }



}
