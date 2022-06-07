package pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.controllers;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.util.NestedServletException;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.mockusers.WithMockCustomUser;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.models.*;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.payload.request.CodeGenerateRequest;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.payload.request.TransactionRequest;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.repository.*;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.security.WebSecurityConfig;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.services.AuthorizeTransaction;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.services.CurrencyRates;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.services.UserDetailsImpl;

import java.security.NoSuchAlgorithmException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@AutoConfigureMockMvc
@SpringBootTest
@Import({WebSecurityConfig.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class TransactionBeginTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    UserRepository userRepository;
    @Autowired
    BankAccountRepository bankAccountRepository;
    @Autowired
    BankAccountTypeRepository bankAccountTypeRepository;
    @Autowired
    TransactionRepository transactionRepository;
    @Autowired
    TransactionTypeRepository transactionTypeRepository;
    @Autowired
    TransactionStatusRepository transactionStatusRepository;
    @Autowired
    CurrencyRates currencyRates;
    @Autowired
    AuthorizeTransaction authorizeTransaction;
    @Autowired
    AuthorizationCodeRepository authorizationCodeRepository;

    String json;
    ObjectMapper mapper = new ObjectMapper();
    TransactionRequest request;

    User user1;
    BankAccount bankAccount1;
    UserDetailsImpl user1Details;
    User user2;
    BankAccount bankAccount2;

    AuthorizationCode code;

    @BeforeEach
    public void initializeRequest() throws NoSuchAlgorithmException {
        user1=new User("usernamefine","user@yourdomain.com","password",
                "testname", "testsurname", "012345678910", "PPP123123",
                "testaddress", "testcorrespondence");
        userRepository.save(user1);
        user1Details = UserDetailsImpl.build(user1);

        user2=new User("testuserS","test@test.com","password","testname",
                "testsurname","100123456789","KKK321321","testaddress",
                "testcorrespondence");
        userRepository.save(user2);

        BankAccountType type = new BankAccountType(1.0,1.0,1.0,"Test");
        bankAccountTypeRepository.save(type);

        bankAccount1 = new BankAccount(type,user1,1000.0,"12345678901234567890123456");
        bankAccount2 = new BankAccount(type,user2,1000.0,"12345678901234567890654321");
        bankAccountRepository.save(bankAccount1);
        bankAccountRepository.save(bankAccount2);

        code = new AuthorizationCode(user1,0);
        authorizationCodeRepository.save(code);

        request = new TransactionRequest();
        request.setType("TRANSFER");
        request.setAmount("100.00");
        request.setFrom("12345678901234567890123456");
        request.setTo("12345678901234567890654321");
        request.setTransferTitle("Transfer");
        request.setAuthorizationData(code.getCode());

        for(long i=1L;i<=EnumTransactionType.values().length;i++) {
            TransactionType transactionType = new TransactionType();
            transactionType.setName(EnumTransactionType.values()[(int)i-1]);
            transactionType.setId(i);
            transactionTypeRepository.save(transactionType);
        }

        for(long i=1L;i<=EnumTransactionStatus.values().length;i++) {
            TransactionStatus transactionStatus = new TransactionStatus();
            transactionStatus.setName(EnumTransactionStatus.values()[(int)i-1]);
            transactionStatus.setId(i);
            transactionStatusRepository.save(transactionStatus);
        }


    }

    @Test
    @WithMockCustomUser
    public void fromNotFoundInDatabaseThrowsException() throws Exception {
        request.setFrom("12345678901234567890654321");
        json = new ObjectMapper().writeValueAsString(request);
        try {
            mockMvc.perform(MockMvcRequestBuilders.post("/api/transactions/transaction_begin").content(json)
                    .contentType(MediaType.APPLICATION_JSON));
        } catch(NestedServletException ex) {
            assertInstanceOf(RuntimeException.class, ex.getCause());
            assertEquals("No account with that number and/or user found",ex.getCause().getMessage());
        }
    }

    @Test
    @WithMockCustomUser
    public void fromUsedFromDifferentUserThrowsException() throws Exception {
        request.setFrom("01010101010101010101010101");
        json = new ObjectMapper().writeValueAsString(request);
        try {
            mockMvc.perform(MockMvcRequestBuilders.post("/api/transactions/transaction_begin").content(json)
                    .contentType(MediaType.APPLICATION_JSON));
        } catch(NestedServletException ex) {
            assertInstanceOf(RuntimeException.class, ex.getCause());
            assertEquals("No account with that number and/or user found",ex.getCause().getMessage());
        }
    }

    @Test
    @WithMockCustomUser
    public void wronglyAuthorizedTransactionIsInvalid() throws Exception {
        request.setAuthorizationData(new StringBuilder().append(code.getCode()).reverse().toString());
        json = new ObjectMapper().writeValueAsString(request);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/transactions/transaction_begin").content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> assertEquals("Authorization failed", mapper.readValue(result.getResponse().getContentAsString(), Map.class).get("message")))
                .andExpect(result -> assertEquals(400, result.getResponse().getStatus()))
                .andExpect(result -> assertEquals("REJECTED",mapper.readValue(result.getResponse().getContentAsString(), Map.class).get("status")));
    }

    @Test
    @WithMockCustomUser
    public void toEqualsFromThrowsExceptionIsInvalid() throws Exception {
        request.setTo(request.getFrom());
        json = new ObjectMapper().writeValueAsString(request);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/transactions/transaction_begin").content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> assertEquals("Cannot send a transfer to same account", mapper.readValue(result.getResponse().getContentAsString(), Map.class).get("message")))
                .andExpect(result -> assertEquals(400, result.getResponse().getStatus()))
                .andExpect(result -> assertEquals("REJECTED",mapper.readValue(result.getResponse().getContentAsString(), Map.class).get("status")));
    }

    @Test
    @WithMockCustomUser
    public void notEnoughMoneyIsInvalid() throws Exception {
        request.setAmount("1200.00");
        json = new ObjectMapper().writeValueAsString(request);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/transactions/transaction_begin").content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> assertEquals("Not enough cash to process transaction", mapper.readValue(result.getResponse().getContentAsString(), Map.class).get("message")))
                .andExpect(result -> assertEquals(400, result.getResponse().getStatus()))
                .andExpect(result -> assertEquals("REJECTED",mapper.readValue(result.getResponse().getContentAsString(), Map.class).get("status")));
    }

    @Test
    @WithMockCustomUser
    public void notEnoughMoneyWithCurrencyIsInvalid() throws Exception {
        request.setCurrencyCode("EUR");
        request.setAmount("999.00");
        json = new ObjectMapper().writeValueAsString(request);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/transactions/transaction_begin").content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> assertEquals("Not enough cash to process transaction", mapper.readValue(result.getResponse().getContentAsString(), Map.class).get("message")))
                .andExpect(result -> assertEquals(400, result.getResponse().getStatus()))
                .andExpect(result -> assertEquals("REJECTED",mapper.readValue(result.getResponse().getContentAsString(), Map.class).get("status")));
    }
    
    @Test
    @WithMockCustomUser
    public void internalTransactionWorks() throws Exception {
        json = new ObjectMapper().writeValueAsString(request);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/transactions/transaction_begin").content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> assertEquals("Transfer begin completed succesfully", mapper.readValue(result.getResponse().getContentAsString(), Map.class).get("message")))
                .andExpect(result -> assertEquals(200, result.getResponse().getStatus()))
                .andExpect(result -> assertEquals("FINALIZED",mapper.readValue(result.getResponse().getContentAsString(), Map.class).get("status")));
        Transaction transaction = transactionRepository.findFirstByOrderByIdAsc().get();
        assertFalse(transaction.getIsExternal());
        bankAccount1 = bankAccountRepository.findByAccountNumber(bankAccount1.getAccountNumber()).get();
        bankAccount2 = bankAccountRepository.findByAccountNumber(bankAccount2.getAccountNumber()).get();
        assertEquals(900.0,bankAccount1.getMoney());
        assertEquals(1100.0,bankAccount2.getMoney());
    }

    @Test
    @WithMockCustomUser
    public void externalTransactionWorks() throws Exception {
        request.setTo("01010101010101010101010101");
        json = new ObjectMapper().writeValueAsString(request);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/transactions/transaction_begin").content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> assertEquals("Transfer begin completed succesfully", mapper.readValue(result.getResponse().getContentAsString(), Map.class).get("message")))
                .andExpect(result -> assertEquals(200, result.getResponse().getStatus()))
                .andExpect(result -> assertEquals("FINALIZED",mapper.readValue(result.getResponse().getContentAsString(), Map.class).get("status")));
        Transaction transaction = transactionRepository.findFirstByOrderByIdAsc().get();
        bankAccount1 = bankAccountRepository.findByAccountNumber(bankAccount1.getAccountNumber()).get();
        assertTrue(transaction.getIsExternal());
        assertEquals(900.0,bankAccount1.getMoney());
    }

}
