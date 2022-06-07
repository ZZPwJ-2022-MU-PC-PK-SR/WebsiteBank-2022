package pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.services;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.mockusers.WithMockCustomUser;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.models.AuthorizationCode;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.models.User;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.payload.request.CodeValidateRequest;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.repository.AuthorizationCodeRepository;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.repository.UserRepository;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.security.WebSecurityConfig;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.services.CheckCode;

import java.security.NoSuchAlgorithmException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@AutoConfigureMockMvc
@SpringBootTest
@Import({WebSecurityConfig.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class AuthorizationValidateCodeTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuthorizationCodeRepository authorizationCodeRepository;
    @Autowired
    private CheckCode checkCode;

    private AuthorizationCode firstCode;
    private AuthorizationCode secondCode;
    private AuthorizationCode lastCode;

    private UserDetailsImpl userDetails;

    @BeforeEach
    public void validateInit() throws NoSuchAlgorithmException {
        User user = new User("usernamefine","user@yourdomain.com","password",
                "testname", "testsurname", "012345678910", "PPP123123",
                "testaddress", "testcorrespondence");
        userRepository.save(user);
        userDetails = UserDetailsImpl.build(user);

        firstCode = new AuthorizationCode(user,0);
        secondCode = new AuthorizationCode(user,1);
        lastCode = new AuthorizationCode(user,2);

        authorizationCodeRepository.save(firstCode);
        authorizationCodeRepository.save(secondCode);
        authorizationCodeRepository.save(lastCode);

    }

    @WithMockCustomUser
    @Test
    public void checkCodeFirstValidTest() {
        assertTrue(checkCode.checkCode(firstCode.getCode(),userDetails));
    }

    @WithMockCustomUser
    @Test
    public void checkCodeSecondAndLastInvalidTest() {
        assertFalse(checkCode.checkCode(secondCode.getCode(),userDetails));
        assertFalse(checkCode.checkCode(lastCode.getCode(),userDetails));
    }

    @WithMockCustomUser
    @Test
    public void checkCodeSecondValidAfterFirstUsed() {
        checkCode.checkCode(firstCode.getCode(),userDetails);
        assertTrue(checkCode.checkCode(secondCode.getCode(),userDetails));
    }

    @WithMockCustomUser
    @Test
    public void checkCodeCannotUseSameCodeTwice() {
        checkCode.checkCode(firstCode.getCode(),userDetails);
        assertFalse(checkCode.checkCode(firstCode.getCode(),userDetails));

    }

    @WithMockCustomUser
    @Test
    public void checkCodeReturnFalseIfNoCodesPresent() {

        firstCode.setActive(false);
        secondCode.setActive(false);
        lastCode.setActive(false);

        authorizationCodeRepository.save(firstCode);
        authorizationCodeRepository.save(secondCode);
        authorizationCodeRepository.save(lastCode);

        assertFalse(checkCode.checkCode(firstCode.getCode(),userDetails));
    }

}
