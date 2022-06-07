package pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.controllers;


import com.fasterxml.jackson.core.JsonProcessingException;
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
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.models.AuthorizationCode;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.models.User;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.payload.request.CodeGenerateRequest;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.repository.AuthorizationCodeRepository;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.repository.UserRepository;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.security.WebSecurityConfig;

import java.util.ArrayList;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@AutoConfigureMockMvc
@SpringBootTest
@Import({WebSecurityConfig.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AuthorizationGenerateCodesTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuthorizationCodeRepository authorizationCodeRepository;

    private String json;
    private ObjectMapper mapper = new ObjectMapper();

    @BeforeAll
    public void initialize() {
        userRepository.deleteAll();
        userRepository.save(new User("usernamefine","user@yourdomain.com","password",
                "testname", "testsurname", "012345678910", "PPP123123",
                "testaddress", "testcorrespondence"));
    }

    @BeforeEach
    public void generateInit() throws JsonProcessingException {
        CodeGenerateRequest request = new CodeGenerateRequest();
        request.setUsername("usernamefine");
        json = new ObjectMapper().writeValueAsString(request);
    }


    @WithMockCustomAdmin
    @Test
    public void generateCodesAuthorizedTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/authcodes/generate_codes").content(json).
                contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.ALL_VALUE)).andExpect(
                result -> assertEquals(50, ((ArrayList<String>) mapper.readValue(result.getResponse().getContentAsString(), Map.class).get("codes")).size()))
                .andExpect(result-> assertEquals(200,result.getResponse().getStatus()));
    }

    @WithMockCustomAdmin
    @Test
    public void generateCodesWhenAlreadyGeneratedTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/authcodes/generate_codes").content(json).
                contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.ALL_VALUE));
        mockMvc.perform(MockMvcRequestBuilders.post("/api/authcodes/generate_codes").content(json).
                contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.ALL_VALUE)).andExpect(
                result -> assertEquals("User has active codes", mapper.readValue(result.getResponse().getContentAsString(), Map.class).get("message")))
                .andExpect(result -> assertEquals(400,result.getResponse().getStatus()));
    }


    @WithMockCustomUser
    @Test
    public void generateCodesUnauthorizedUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/authcodes/generate_codes").content(json).
                contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.ALL_VALUE))
                .andExpect(result -> assertEquals("Unauthorized", (mapper.readValue(result.getResponse().getContentAsString(), Map.class).get("error"))))
                .andExpect(result -> assertEquals(401,result.getResponse().getStatus()));
    }

    @Test
    public void generateCodesUnauthorizedAnonymous() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/authcodes/generate_codes").content(json).
                contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.ALL_VALUE))
                .andExpect(result -> assertEquals("Unauthorized", (mapper.readValue(result.getResponse().getContentAsString(), Map.class).get("error"))))
                .andExpect(result -> assertEquals(401,result.getResponse().getStatus()));
    }

    @WithMockCustomAdmin
    @Test
    public void generateCodesOnMissingUser() throws Exception {
        CodeGenerateRequest request = new CodeGenerateRequest();
        request.setUsername("missinguser");
        json = new ObjectMapper().writeValueAsString(request);
        try {
            mockMvc.perform(MockMvcRequestBuilders.post("/api/authcodes/generate_codes").content(json)
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
