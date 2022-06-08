package pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.controllers;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.models.BankAccount;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.models.BankAccountType;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.models.Transaction;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.models.User;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.payload.request.BankAccountRequest;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.payload.request.TransactionRequest;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.payload.response.BankAccountResponse;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.payload.response.CodesResponse;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.payload.response.MessageResponse;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.repository.*;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.services.CheckCode;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.services.UserDetailsImpl;

import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

@CrossOrigin(origins = "*", maxAge = 3600)
@Controller
@RequestMapping("/api/bank_account")
@Log4j2
public class BankAccountController {
    @Autowired
    private BankAccountRepository bankAccountRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BankAccountTypeRepository bankAccountTypeRepository;
    @Autowired
    private CheckCode checkCode;

    @PostMapping("/add_new")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> addNewBankAccount(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                               @RequestBody BankAccountRequest bankAccountRequest) {
        if(!checkCode.checkCode(bankAccountRequest.getCode(), userDetails)) {
            return ResponseEntity.badRequest().body(new MessageResponse("Bad Authentication Code"));
        }
        Random rand = new Random();
        BankAccountType bankAccountType;
        try {
            bankAccountType = bankAccountTypeRepository.findById(bankAccountRequest.getTypeId()).orElseThrow(RuntimeException::new);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: Bank Account Type with id=" + bankAccountRequest.getTypeId() + " is not found."));
        }
        User user = userRepository.findById(userDetails.getId()).orElseThrow(() ->
                new RuntimeException("Error: User is not found."));
        StringBuilder accountNumber = new StringBuilder();
        for(int i = 1; i <= 26; i++) {
            accountNumber.append(rand.nextInt(10));
        }
        BankAccount bankAccount = new BankAccount(bankAccountType, user, 0.0, String.valueOf(accountNumber));
        bankAccountRepository.save(bankAccount);
        return ResponseEntity.ok(new MessageResponse("New Bank Account Added!"));
    }

    @GetMapping("/get")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getBankAccounts(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        try {
            List<BankAccount> bankAccountList = bankAccountRepository.findByUserId(userDetails.getId());
            List<BankAccountResponse> bankAccountList1 = new ArrayList<>();
            for (BankAccount b : bankAccountList) {
                bankAccountList1.add(new BankAccountResponse(b));
            }
            return ResponseEntity.ok(bankAccountList1);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
