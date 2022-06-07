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

import static org.junit.jupiter.api.Assertions.*;

@AutoConfigureMockMvc
@SpringBootTest
@Import({WebSecurityConfig.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class AuthorizeTransactionTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    UserRepository userRepository;
    @Autowired
    TransactionTypeRepository transactionTypeRepository;
    @Autowired
    AuthorizeTransaction authorizeTransaction;
    @Autowired
    AuthorizationCodeRepository authorizationCodeRepository;


    User user1;
    UserDetailsImpl user1Details;

    TransactionType type;

    @BeforeEach
    public void initializeRequest() throws NoSuchAlgorithmException {
        user1=new User("usernamefine","user@yourdomain.com","password",
                "testname", "testsurname", "012345678910", "PPP123123",
                "testaddress", "testcorrespondence");
        userRepository.save(user1);
        user1Details = UserDetailsImpl.build(user1);
        type = new TransactionType();
    }

    @Test
    public void falseCodeReturnsFalse() throws NoSuchAlgorithmException {
        AuthorizationCode code = new AuthorizationCode(user1,0);
        authorizationCodeRepository.save(code);
        String reverseCode = new StringBuilder().append(code.getCode()).reverse().toString();
        type.setName(EnumTransactionType.TRANSFER);
        assertFalse(authorizeTransaction.authorizeTransaction(type,user1Details,reverseCode));
    }

    @Test
    public void trueCodeReturnsTrue() throws NoSuchAlgorithmException {
        AuthorizationCode code = new AuthorizationCode(user1,0);
        authorizationCodeRepository.save(code);
        type.setName(EnumTransactionType.TRANSFER);
        assertTrue(authorizeTransaction.authorizeTransaction(type,user1Details,code.getCode()));
    }

    @Test
    public void trueDzikReturnsTrue() {
        type.setName(EnumTransactionType.DZIK);
        assertTrue(authorizeTransaction.authorizeTransaction(type,user1Details, authorizeTransaction.getCurrentUserDzik(user1Details)));
    }

    @Test
    public void falseDzikReturnsFalse() {
        type.setName(EnumTransactionType.DZIK);
        assertFalse(authorizeTransaction.authorizeTransaction(type,user1Details, "000000"));
    }
}
