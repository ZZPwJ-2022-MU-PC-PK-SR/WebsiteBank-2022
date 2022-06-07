package pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.mockusers.WithMockCustomUser;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.mockusers.WithMockUserPeter;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.models.User;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.payload.request.ChangeDataRequest;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.payload.request.CodeGenerateRequest;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.repository.AuthorizationCodeRepository;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.repository.UserRepository;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.security.WebSecurityConfig;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.services.UserDetailsImpl;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@Import({WebSecurityConfig.class})
public class OptionControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuthorizationCodeRepository authorizationCodeRepository;

    String json;
    ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    private void initalize(){

    }


    @Test
    @WithMockUserPeter
    public void changeDataUserByCode() throws Exception {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow(() -> new RuntimeException("No user with such username found"));;
        ChangeDataRequest changeDataRequest = new ChangeDataRequest();
        changeDataRequest.setCode(authorizationCodeRepository.findTopByActiveTrueAndUserOrderByOrderNo(user).getCode());
        changeDataRequest.setEmail("Testowy@wp.pl");
        changeDataRequest.setSurname("testzmiana");
        changeDataRequest.setPassword("012345678910");
        changeDataRequest.setAddressLiving("New addres 22");
        changeDataRequest.setAddressCorrespondence("New addres 22");
        changeDataRequest.setName("Test");
        json = new ObjectMapper().writeValueAsString(changeDataRequest);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/changedata").content(json).
                contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.ALL_VALUE)).andExpect(status().isOk()).andExpect(result -> assertEquals("{\"message\":\"User information has been correctly changed!\"}", result.getResponse().getContentAsString()));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/getuseradata").
                        contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL_VALUE))
                .andExpect(result -> assertEquals("{\"Email\":\"Testowy@wp.pl\",\"Surname\":\"testzmiana\",\"Address living\":\"New addres 22\",\"Name\":\"Test\",\"Address correspondence\":\"New addres 22\"}", result.getResponse().getContentAsString()));
    }


    @Test
    @WithMockUserPeter
    public void getDataUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/getuseradata").
                        contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL_VALUE))
                .andExpect(result -> assertEquals("{\"Email\":\"alfonsy@yourdomain.com\",\"Surname\":\"Barabasz\",\"Address living\":\"testaddress 22\",\"Name\":\"Alfonsy\",\"Address correspondence\":\"testcorrespondence 33\"}", result.getResponse().getContentAsString()));
    }






}
