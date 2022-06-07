package pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.util.NestedServletException;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.mockusers.WithMockCustomAdmin;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.mockusers.WithMockCustomUser;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.models.*;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.payload.request.AccountRequest;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.payload.request.BlockCardRequest;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.payload.request.LoginRequest;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.payload.request.RequestNewCardRequest;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.repository.*;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.services.CheckCode;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext
public class PageController2Test {


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    CheckCode checkCode;
//    @Mock
    @Autowired
    CardRepository cardRepository;
    @Autowired
    BankAccountRepository bankAccountRepository;
    @Autowired
    BankAccountTypeRepository bankAccountTypeRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    AuthorizationCodeRepository authorizationCodeRepository;
//    @Mock
//    BankAccountTypeRepository bankAccountTypeRepository;
//    @Mock
//    TransactionRepository transactionRepository;
//    @Mock
//    TransactionTypeRepository transactionTypeRepository;
//    @Mock
//    TransactionStatusRepository transactionStatusRepository;
    @Autowired
    PasswordEncoder encoder;

    @InjectMocks
    PageController pageController;

    User user;

    @BeforeEach
    public void init(){
        String username = "user";
        String password = "pwd";
        String email = "jonras@wp.pl";
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setPassword(password);
        loginRequest.setUsername(username);
        user = new User(username, email, password);
        user.setName("cos");
        user.setSurname("cos");
        user.setAddressCorrespondence("cos");
        user.setPersonalId("cso");
        user.setAddressLiving("cos");
        user.setIdCardNumber("cs");
        Role role = new Role();
        role.setId(1);
        role.setName(EnumRole.ROLE_USER);
        user.setRoles(new HashSet<>(Arrays.asList(role)));
    }


    @Test
    @WithMockCustomUser
    public void BlockTheCardTest() throws Exception{
        Date date = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.YEAR,2);
        Date newDate = c.getTime();
        BankAccountType bankAccountType = new BankAccountType(4.0,4.0,4.0,"1");
        BankAccount bankAccount = new BankAccount(bankAccountType,user,1000.0,"11111111111111111111111111");
        Cards card = new Cards("11111111111111111111111111",newDate,"Active",encoder.encode("123455555555"));
        card.setId(1L);
        bankAccountType.setId(1L);
        List<Optional<BankAccount>> list = new ArrayList<>();
        list.add(Optional.of(bankAccount));
        BlockCardRequest blockCardRequest = new BlockCardRequest();
        blockCardRequest.setCardID(1L);
        String json  = new ObjectMapper().writeValueAsString(blockCardRequest);
        cardRepository.save(card);
        mockMvc.perform(get("/api/logged/block_the_card").content(json).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.ALL_VALUE)).andExpect(status().isOk());
    }

    @Test
    @WithMockCustomAdmin
    public void account_percentage_moneyTest() throws Exception{
        AccountRequest blockCardRequest = new AccountRequest();
        blockCardRequest.setAccountNumber("11111111111111111111111111");
        String json  = new ObjectMapper().writeValueAsString(blockCardRequest);
        BankAccountType bankAccountType = new BankAccountType(4.0,4.0,4.0,"1");
        bankAccountType.setId(1L);
        BankAccount bankAccount = new BankAccount(bankAccountType,user,1000.0,"11111111111111111111111111");
        Exception exception =  assertThrows(NestedServletException.class, () ->{
            mockMvc.perform(get("/api/logged/account_percentage_money").content(json).contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.ALL_VALUE));
        });

    }

    @Test
    @WithMockCustomUser
    public void request_new_cardTest() throws Exception{

        RequestNewCardRequest blockCardRequest = new RequestNewCardRequest();
        blockCardRequest.setCode("7060");
        String json  = new ObjectMapper().writeValueAsString(blockCardRequest);
        BankAccountType bankAccountType = new BankAccountType(4.0,4.0,4.0,"1");
        bankAccountType.setId(1L);
//        AuthorizationCode authorizationCode = new AuthorizationCode(user,21);
//        authorizationCode.setId(6L);
//        authorizationCodeRepository.save(authorizationCode);
//        BankAccount bankAccount = new BankAccount(bankAccountType,user,1000.0,"11111111111111111111111111");
        Exception exception =  assertThrows(NestedServletException.class, () ->{
            mockMvc.perform(get("/api/logged/request_new_card").content(json).contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.ALL_VALUE));
        });

    }





}
