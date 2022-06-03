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
import org.springframework.web.util.NestedServletException;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.mockusers.WithMockCustomAdmin;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.mockusers.WithMockCustomUser;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.models.User;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.payload.request.CodeGenerateRequest;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.repository.AuthorizationCodeRepository;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.repository.UserRepository;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.security.WebSecurityConfig;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@AutoConfigureMockMvc
@SpringBootTest
@Import({WebSecurityConfig.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AuthorizationCompromiseCodesTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuthorizationCodeRepository authorizationCodeRepository;

    String json;
    ObjectMapper mapper = new ObjectMapper();

    @BeforeAll
    public void initialize() {
        userRepository.deleteAll();
        userRepository.save(new User("usernamefine","user@yourdomain.com","password",
                "testname", "testsurname", "012345678910", "PPP123123",
                "testaddress", "testcorrespondence"));
    }

    @BeforeEach
    public void compromiseInit() throws Exception {
        CodeGenerateRequest request = new CodeGenerateRequest();
        request.setUsername("usernamefine");
        json = mapper.writeValueAsString(request);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/authcodes/generate_codes").content(json).
                contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.ALL_VALUE));
    }

    @Test
    @WithMockCustomAdmin
    public void compromiseCodesAuthorizedTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/authcodes/compromise_codes").content(json)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL_VALUE))
                .andExpect(result -> assertEquals("Codes sucessfully compromised", mapper.readValue(result.getResponse().getContentAsString(), Map.class).get("message")))
                .andExpect(result -> assertEquals(200, result.getResponse().getStatus()));
    }

    @Test
    @WithMockCustomAdmin
    public void compromiseCodesWhenNoCodesTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/authcodes/compromise_codes").content(json)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.ALL_VALUE));
        mockMvc.perform(MockMvcRequestBuilders.post("/api/authcodes/compromise_codes").content(json)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL_VALUE))
                .andExpect(result -> assertEquals("User has no active codes", mapper.readValue(result.getResponse().getContentAsString(), Map.class).get("message")))
                .andExpect(result -> assertEquals(400, result.getResponse().getStatus()));
    }

    @Test
    @WithMockCustomUser
    public void compromiseCodesWhenUnauthorizedUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/authcodes/compromise_codes").content(json)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL_VALUE))
                .andExpect(result -> assertEquals("Unauthorized", mapper.readValue(result.getResponse().getContentAsString(), Map.class).get("error")))
                .andExpect(result -> assertEquals(401, result.getResponse().getStatus()));
    }

    @Test
    @WithMockCustomUser
    public void compromiseCodesWhenAnonymousUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/authcodes/compromise_codes").content(json)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL_VALUE))
                .andExpect(result -> assertEquals("Unauthorized", mapper.readValue(result.getResponse().getContentAsString(), Map.class).get("error")))
                .andExpect(result -> assertEquals(401, result.getResponse().getStatus()));
    }

    @WithMockCustomAdmin
    @Test
    public void compromiseCodesOnMissingUser() throws Exception {
        CodeGenerateRequest request = new CodeGenerateRequest();
        request.setUsername("missinguser");
        json = new ObjectMapper().writeValueAsString(request);
        try {
            mockMvc.perform(MockMvcRequestBuilders.post("/api/authcodes/compromise_codes").content(json)
                    .contentType(MediaType.APPLICATION_JSON));
        } catch(NestedServletException ex) {
            assertInstanceOf(RuntimeException.class, ex.getCause());
            assertEquals("No user with such username found",ex.getCause().getMessage());
        }
    }

    @AfterAll
    public void clearTables() {
        authorizationCodeRepository.deleteAll();
        userRepository.deleteAll();
    }
}
