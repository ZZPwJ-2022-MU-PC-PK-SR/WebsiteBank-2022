package pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.services;


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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.util.NestedServletException;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.models.*;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.payload.request.CodeGenerateRequest;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.payload.request.TransactionRequest;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.repository.*;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.security.WebSecurityConfig;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.services.AuthorizeTransaction;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.services.CurrencyRates;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.services.UserDetailsImpl;

import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import static org.junit.jupiter.api.Assertions.*;

@AutoConfigureMockMvc
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class AuthorizeCardsTransactionTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TransactionTypeRepository transactionTypeRepository;
    @Autowired
    private AuthorizeTransaction authorizeTransaction;
    @Autowired
    private BankAccountTypeRepository bankAccountTypeRepository;
    @Autowired
    private BankAccountRepository bankAccountRepository;
    @Autowired
    private CardRepository cardRepository;
    @Autowired
    private PasswordEncoder encoder;


    User user1;
    UserDetailsImpl user1Details;
    private BankAccountType accountType;
    private BankAccount account;
    private Cards card;

    TransactionType type;

    @BeforeEach
    public void initializeRequest() {
        user1=new User("usernamefine","user@yourdomain.com","password",
                "testname", "testsurname", "012345678910", "PPP123123",
                "testaddress", "testcorrespondence");
        userRepository.save(user1);
        user1Details = UserDetailsImpl.build(user1);
        accountType = new BankAccountType(1.0,1.0,1.0,"Test");
        bankAccountTypeRepository.save(accountType);
        account = new BankAccount(accountType,user1,1000.0,"12345678901234567890123456");
        bankAccountRepository.save(account);
        type = new TransactionType();
        type.setName(EnumTransactionType.CARD);
    }

    @Test
    public void missingCardReturnsFalse() throws ParseException {
        card = new Cards("12345678901234567890123456",new SimpleDateFormat("yyyy-MM-dd").parse("2040-01-01"),"Active",encoder.encode("123412341234"));
        cardRepository.save(card);
        assertFalse(authorizeTransaction.authorizeTransaction(type,user1Details,"432143214321#12345678901234567890123456"));
    }
    @Test
    public void wrongAccountNumberReturnsFalse() throws ParseException {
        card = new Cards("11111111111111111111111111",new SimpleDateFormat("yyyy-MM-dd").parse("2040-01-01"),"Active",encoder.encode("123412341234"));
        cardRepository.save(card);
        assertFalse(authorizeTransaction.authorizeTransaction(type,user1Details,"123412341234#12345678901234567890123456"));
    }
    @Test
    public void cardInActiveReturnsFalse() throws ParseException {
        card = new Cards("12345678901234567890123456",new SimpleDateFormat("yyyy-MM-dd").parse("2040-01-01"),"inActive",encoder.encode("123412341234"));
        cardRepository.save(card);
        assertFalse(authorizeTransaction.authorizeTransaction(type,user1Details,"123412341234#12345678901234567890123456"));
    }

    @Test
    public void cardExpiredReturnsFalse() throws ParseException {
        card = new Cards("12345678901234567890123456",new SimpleDateFormat("yyyy-MM-dd").parse("2015-01-01"),"Active",encoder.encode("123412341234"));
        cardRepository.save(card);
        assertFalse(authorizeTransaction.authorizeTransaction(type,user1Details,"123412341234#12345678901234567890123456"));
    }

    @Test
    public void correctCardReturnsTrue() throws ParseException {
        card = new Cards("12345678901234567890123456",new SimpleDateFormat("yyyy-MM-dd").parse("2040-01-01"),"Active",encoder.encode("123412341234"));
        cardRepository.save(card);
        assertTrue(authorizeTransaction.authorizeTransaction(type,user1Details,"123412341234#12345678901234567890123456"));
    }
}
