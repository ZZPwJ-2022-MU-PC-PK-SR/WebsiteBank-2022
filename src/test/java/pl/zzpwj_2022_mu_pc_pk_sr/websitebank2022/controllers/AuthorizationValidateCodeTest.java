package pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.controllers;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.mockusers.WithMockCustomUser;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.models.AuthorizationCode;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.models.User;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.payload.request.CodeValidateRequest;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.repository.AuthorizationCodeRepository;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.repository.UserRepository;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.security.WebSecurityConfig;

import java.security.NoSuchAlgorithmException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@AutoConfigureMockMvc
@SpringBootTest
@Import({WebSecurityConfig.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AuthorizationValidateCodeTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuthorizationCodeRepository authorizationCodeRepository;

    CodeValidateRequest request;
    AuthorizationCode firstCode;
    AuthorizationCode secondCode;
    AuthorizationCode lastCode;

    String json;
    ObjectMapper mapper = new ObjectMapper();

    @BeforeAll
    public void initialize() throws NoSuchAlgorithmException {
        userRepository.deleteAll();
        User user = new User("usernamefine","user@yourdomain.com","password",
                "testname", "testsurname", "012345678910", "PPP123123",
                "testaddress", "testcorrespondence");
        userRepository.save(user);

        firstCode = new AuthorizationCode(user,0);
        secondCode = new AuthorizationCode(user,1);
        lastCode = new AuthorizationCode(user,2);

        authorizationCodeRepository.save(firstCode);
        authorizationCodeRepository.save(secondCode);
        authorizationCodeRepository.save(lastCode);
    }

    @BeforeEach
    public void compromiseInit() {
        firstCode.setActive(true);
        secondCode.setActive(true);
        lastCode.setActive(true);

        authorizationCodeRepository.save(firstCode);
        authorizationCodeRepository.save(secondCode);
        authorizationCodeRepository.save(lastCode);

        request = new CodeValidateRequest();
    }

    @WithMockCustomUser
    @Test
    public void checkCodeFirstValidTest() throws Exception {
        request.setCode(firstCode.getCode());
        json = mapper.writeValueAsString(request);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/authcodes/validate_code").content(json)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL_VALUE))
                .andExpect(result-> assertTrue((Boolean) mapper.readValue(result.getResponse().getContentAsString(), Map.class).get("valid")));
    }

    @WithMockCustomUser
    @Test
    public void checkCodeSecondAndLastInvalidTest() throws Exception {

        request.setCode(secondCode.getCode());
        json = mapper.writeValueAsString(request);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/authcodes/validate_code").content(json)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL_VALUE))
                .andExpect(result-> assertFalse((Boolean) mapper.readValue(result.getResponse().getContentAsString(), Map.class).get("valid")));

        request.setCode(lastCode.getCode());
        json = mapper.writeValueAsString(request);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/authcodes/validate_code").content(json)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL_VALUE))
                .andExpect(result-> assertFalse((Boolean) mapper.readValue(result.getResponse().getContentAsString(), Map.class).get("valid")));
    }

    @WithMockCustomUser
    @Test
    public void checkCodeSecondValidAfterFirstUsed() throws Exception {

        request.setCode(firstCode.getCode());
        json = mapper.writeValueAsString(request);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/authcodes/validate_code").content(json)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.ALL_VALUE));

        request.setCode(secondCode.getCode());
        json = mapper.writeValueAsString(request);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/authcodes/validate_code").content(json)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL_VALUE))
                .andExpect(result-> assertTrue((Boolean) mapper.readValue(result.getResponse().getContentAsString(), Map.class).get("valid")));

    }

    @WithMockCustomUser
    @Test
    public void checkCodeCannotUseSameCodeTwice() throws Exception {

        request.setCode(firstCode.getCode());
        json = mapper.writeValueAsString(request);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/authcodes/validate_code").content(json)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.ALL_VALUE));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/authcodes/validate_code").content(json)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL_VALUE))
                .andExpect(result-> assertFalse((Boolean) mapper.readValue(result.getResponse().getContentAsString(), Map.class).get("valid")));

    }

    @WithMockCustomUser
    @Test
    public void checkCodeReturnFalseIfNoCodesPresent() throws Exception {

        firstCode.setActive(false);
        secondCode.setActive(false);
        lastCode.setActive(false);

        authorizationCodeRepository.save(firstCode);
        authorizationCodeRepository.save(secondCode);
        authorizationCodeRepository.save(lastCode);

        request.setCode(firstCode.getCode());
        json = mapper.writeValueAsString(request);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/authcodes/validate_code").content(json)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL_VALUE))
                .andExpect(result-> assertFalse((Boolean) mapper.readValue(result.getResponse().getContentAsString(), Map.class).get("valid")));
    }

    @AfterAll
    public void clearTables() {
        authorizationCodeRepository.deleteAll();
        userRepository.deleteAll();
    }

}
