package pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.controllers;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.mockusers.WithMockCustomUserTransaction;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.models.*;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.payload.request.BankAccountRequest;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.payload.response.BankAccountResponse;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.repository.*;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.security.WebSecurityConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
public class BankAccountTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private BankAccountTypeRepository bankAccountTypeRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuthorizationCodeRepository authorizationCodeRepository;
    @Autowired
    private BankAccountRepository bankAccountRepository;
    private String json;
    private final ObjectMapper mapper = new ObjectMapper();
    private BankAccountRequest bankAccountRequest;
    private List<BankAccountResponse> bankAccountList;

    @BeforeEach
    public void init() throws Exception {
        bankAccountRepository.deleteAll();
        bankAccountList = new ArrayList<>();
        bankAccountRequest = new BankAccountRequest();
        BankAccountType bankAccountType = new BankAccountType(4.0,4.0,4.0,"Normal");
        bankAccountTypeRepository.save(bankAccountType);
        User testUser = new User("usernamefine", "testuser@test.com","testpassword",
                "testname","testsurname","012345678910","PPP123123",
                "testaddress","testcorrespondence");
        testUser.setId(1L);
        userRepository.save(testUser);
        AuthorizationCode code = new AuthorizationCode(testUser,0);
        code.setCode("1234");
        authorizationCodeRepository.save(code);
        BankAccount b1 = new BankAccount(bankAccountType, testUser, 10.0, "12345678901234567890123416");
        BankAccount b2 = new BankAccount(bankAccountType, testUser, 20.0, "12345678901234567890123426");
        bankAccountRepository.save(b1);
        bankAccountRepository.save(b2);
        bankAccountList.add(new BankAccountResponse(b1));
        bankAccountList.add(new BankAccountResponse(b2));

    }

    @Test
    @WithMockCustomUserTransaction
    public void shouldReturnOkStatusAddingBankAccount() throws Exception {
        bankAccountRequest.setTypeId(1L);
        bankAccountRequest.setCode("1234");
        json = new ObjectMapper().writeValueAsString(bankAccountRequest);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/bank_account/add_new").content(json)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.ALL_VALUE))
                .andExpect(status().isOk()).andExpect(result ->
                        assertEquals(result.getResponse().getContentAsString(), "{\"message\":\"New Bank Account Added!\"}"));

    }

    @Test
    @WithMockCustomUserTransaction
    public void shouldReturnBadStatusByWrongTypeId() throws Exception {
        bankAccountRequest.setTypeId(9L);
        bankAccountRequest.setCode("1234");
        json = new ObjectMapper().writeValueAsString(bankAccountRequest);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/bank_account/add_new").content(json)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL_VALUE))
                .andExpect(status().is4xxClientError()).andExpect(
                        result -> assertEquals("Error: Bank Account Type with id=" + bankAccountRequest.getTypeId() + " is not found.",
                                mapper.readValue(result.getResponse().getContentAsString(), Map.class).get("message")));
    }

    @Test
    @WithMockCustomUserTransaction
    public void shouldReturnBadStatusByWrongAuthorizationCode() throws Exception {
        bankAccountRequest.setTypeId(1L);
        bankAccountRequest.setCode("1233");
        json = new ObjectMapper().writeValueAsString(bankAccountRequest);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/bank_account/add_new").content(json)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL_VALUE))
                .andExpect(status().is4xxClientError()).andExpect(
                        result -> assertEquals("Bad Authentication Code",
                                mapper.readValue(result.getResponse().getContentAsString(), Map.class).get("message")));
    }

    @Test
    @WithMockCustomUserTransaction
    public void shouldReturnBankAccounts() throws Exception {
        mockMvc.perform(get("/api/bank_account/get"))
                .andExpect(status().isOk()).andExpect(result ->
                        assertEquals(result.getResponse().getContentAsString(),
                                mapper.writeValueAsString(bankAccountList)));
    }

}