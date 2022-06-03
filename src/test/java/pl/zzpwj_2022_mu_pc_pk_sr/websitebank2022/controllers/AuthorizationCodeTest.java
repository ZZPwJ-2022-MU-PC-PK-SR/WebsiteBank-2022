package pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.controllers;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.util.NestedServletException;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.mockusers.WithMockCustomAdmin;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.mockusers.WithMockCustomUser;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.models.AuthorizationCode;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.models.User;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.payload.request.CodeGenerateRequest;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.payload.request.CodeValidateRequest;
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
public class AuthorizationCodeTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuthorizationCodeRepository authorizationCodeRepository;

    @BeforeAll
    public void initialize() {
        userRepository.save(new User("usernamefine","user@yourdomain.com","password",
                "testname", "testsurname", "012345678910", "PPP123123",
                "testaddress", "testcorrespondence"));
    }

    @Nested
    class generateCodesTestsSuite {
        String json;
        ObjectMapper mapper = new ObjectMapper();

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
    }

    @Nested
    class compromiseCodesTestSuite {
        String json;
        ObjectMapper mapper = new ObjectMapper();

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
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class checkCodeTests {
        CodeValidateRequest request;
        AuthorizationCode firstCode;
        AuthorizationCode secondCode;
        AuthorizationCode lastCode;
        ObjectMapper mapper = new ObjectMapper();
        String json;

        @BeforeAll
        public void comrpomiseInitAll() throws Exception {
            User user = userRepository.findByUsername("usernamefine").get();

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

    }
}
