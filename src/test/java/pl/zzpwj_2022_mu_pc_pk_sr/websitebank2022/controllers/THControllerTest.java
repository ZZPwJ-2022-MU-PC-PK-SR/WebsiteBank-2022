package pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.controllers;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cglib.core.CollectionUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.mockusers.WithMockCustomUserTransaction;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.models.*;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.payload.response.TransactionResponse;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.repository.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@DirtiesContext(methodMode= DirtiesContext.MethodMode.BEFORE_METHOD)
public class THControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private TransactionStatusRepository transactionStatusRepository;
    @Autowired
    private BankAccountRepository bankAccountRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TransactionTypeRepository transactionTypeRepository;
    @Autowired
    private TransactionHistoryRepository thRepository;
    private List<TransactionResponse> transactionList;
    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void init() {
        transactionList = new ArrayList<>();
        TransactionType testTransactionType = new TransactionType();
        testTransactionType.setId(1L);
        transactionTypeRepository.save(testTransactionType);
        TransactionStatus testTransactionStatus = new TransactionStatus();
        testTransactionStatus.setId(1L);
        transactionStatusRepository.save(testTransactionStatus);
        User testUser = new User("testuser", "testuser@test.com","testpassword",
                "testname","testsurname","012345678910","PPP123123",
                "testaddress","testcorrespondence");
        testUser.setId(1L);
        userRepository.save(testUser);
        BankAccount testBankAccount = new BankAccount();
        testBankAccount.setAccountNumber("12345678911111111111111111");
        testBankAccount.setUser(testUser);
        bankAccountRepository.save(testBankAccount);
        for (long i = 1; i < 10; i++) {
            Transaction t = new Transaction();
            t.setId(i);
            t.setType(testTransactionType);
            t.setFrom(testBankAccount);
            t.setToAccountNumber("87654321");
            t.setIsExternal(true);
            t.setStatus(testTransactionStatus);
            t.setTransferTitle("nazwa" + i);
            t.setAmount(1000.0 + i);
            Date date;
            try {
                date = new SimpleDateFormat("yyyy-MM-dd").parse("2020-01-0"+i);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            t.setDate(date);
            transactionList.add(new TransactionResponse(t));
            thRepository.save(t);
        }
    }

    @Test
    @WithMockCustomUserTransaction
    public void shouldReturnStatusOkForTransactionHistory() throws Exception {
        mockMvc.perform(get("/api/transaction_history/")).andExpect(status().isOk());
    }

    @Test
    @WithMockCustomUserTransaction
    public void shouldReturnTransactionHistoryWithSorting() throws Exception {
        transactionList.sort(Comparator.comparing(TransactionResponse::getDate).reversed());
        mockMvc.perform(get("/api/transaction_history/")
                        .param("sortList", "date")
                        .param("sortOrder", "DESC"))
                .andExpect(status().isOk()).andExpect(result -> assertEquals(result.getResponse().getContentAsString(),
                        objectMapper.writeValueAsString(transactionList)));

    }

    @Test
    @WithMockCustomUserTransaction
    public void shouldReturnTransactionHistoryWithFilters() throws Exception {
        transactionList.sort(Comparator.comparing(TransactionResponse::getDate).reversed());
        CollectionUtils.filter(transactionList, o ->
                ((TransactionResponse) o).getAmount() >= 1002 &&
                ((TransactionResponse) o).getAmount() <= 1006 &&
                ((TransactionResponse) o).getDate().matches("2020-01-0[2-4]"));
        mockMvc.perform(get("/api/transaction_history/")
                        .param("greaterThanAmount", "1002")
                        .param("lowerThanAmount", "1006")
                        .param("greaterThanDate", "2020-01-02")
                        .param("lowerThanDate", "2020-01-04")
                        .param("sortList", "date")
                        .param("sortOrder", "DESC"))
                .andExpect(status().isOk()).andExpect(result -> assertEquals(result.getResponse().getContentAsString(),
                        objectMapper.writeValueAsString(transactionList)));

    }

    @Test
    @WithMockCustomUserTransaction
    public void shouldReturnTransactionHistoryWithBadAmountFilters() throws Exception {
        mockMvc.perform(get("/api/transaction_history/")
                        .param("greaterThanAmount", "1004")
                        .param("lowerThanAmount", "1003"))
                .andExpect(status().is4xxClientError()).andExpect(result ->
                        assertEquals(result.getResponse().getContentAsString(),
                                "{\"message\":\"greaterThanAmount can't be greater than lowerThanAmount\"}"));

    }

    @Test
    @WithMockCustomUserTransaction
    public void shouldReturnTransactionHistoryWithBadDateFilters() throws Exception {
        mockMvc.perform(get("/api/transaction_history/")
                        .param("greaterThanDate", "2020-01-06")
                        .param("lowerThanDate", "2020-01-04"))
                .andExpect(status().is4xxClientError()).andExpect(result ->
                        assertEquals(result.getResponse().getContentAsString(),
                                "{\"message\":\"greaterThanDate can't be after lowerThanDate\"}"));

    }

    @Test
    @WithMockCustomUserTransaction
    public void shouldReturnTransactionHistoryWithPagination() throws Exception {
        transactionList.sort(Comparator.comparing(TransactionResponse::getAmount));
        mockMvc.perform(get("/api/transaction_history/")
                            .param("page", "0")
                            .param("size", "3")
                            .param("sortList", "amount")
                            .param("sortOrder", "ASC"))
                    .andExpect(status().isOk()).andExpect(result -> assertEquals(result.getResponse().getContentAsString(),
                            objectMapper.writeValueAsString(transactionList
                                    .subList(0, 3))));
        mockMvc.perform(get("/api/transaction_history/")
                            .param("page", "1")
                            .param("size", "3")
                            .param("sortList", "amount")
                            .param("sortOrder", "ASC"))
                    .andExpect(status().isOk()).andExpect(result -> assertEquals(result.getResponse().getContentAsString(),
                            objectMapper.writeValueAsString(transactionList
                                    .subList(3, 6))));

    }

}
