package pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.models.*;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.payload.request.SignupAdminRequest;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.payload.request.SingupRequest;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.repository.*;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.security.WebSecurityConfig;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.security.jwt.JwtUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Import(WebSecurityConfig.class)
@AutoConfigureMockMvc
public class AdminControllerTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AdminPasswordRepository adminPasswordRepository;
    @Autowired
    private PasswordEncoder encoder;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private MockMvc mockMvc;

    @AfterEach
    public void step(){
        userRepository.deleteAll();
        roleRepository.deleteAll();
        adminPasswordRepository.deleteAll();
    }

    @Test
    public void signupAdminTest() throws Exception {
        SignupAdminRequest signupAdminRequest = new SignupAdminRequest();
        signupAdminRequest.setEmail("jonras@wp.pl");
        signupAdminRequest.setUsername("jonras1234567");
        signupAdminRequest.setRole(new HashSet<>(Arrays.asList("admin")));
        signupAdminRequest.setPassword("password");
        signupAdminRequest.setName("jonras1234567");
        signupAdminRequest.setSurname("jonras1234567");
        signupAdminRequest.setPersonalId("jonras1234567");
        signupAdminRequest.setIdCardNumber("jonras1234567");
        signupAdminRequest.setAddressLiving("jonras1234567");
        signupAdminRequest.setAddressCorrespondence("jonras1234567");
        signupAdminRequest.setPasswordAdmin("bardzomilehasloaaaaaa");
        Role role = new Role();
        role.setId(1);
        role.setName(EnumRole.ROLE_ADMIN);
        roleRepository.save(role);
        PasswordAdmin passwordAdmin = new PasswordAdmin();
        passwordAdmin.setId(1L);
        passwordAdmin.setPassword(encoder.encode("bardzomilehasloaaaaaa"));
        adminPasswordRepository.save(passwordAdmin);
        String json  = new ObjectMapper().writeValueAsString(signupAdminRequest);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/admin/signup").content(json).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.ALL_VALUE)).andExpect(status().isOk()).andExpect(result -> assertEquals("{\"message\":\"Admin registered successfully!\"}", result.getResponse().getContentAsString()));

        User user = userRepository.findByUsername("jonras1234567").orElseThrow(() -> new RuntimeException("No user with such username found"));
        assertEquals(signupAdminRequest.getUsername(), user.getUsername());
        assertEquals(signupAdminRequest.getEmail(), user.getEmail());
        assertEquals(signupAdminRequest.getName(), user.getName());
        assertEquals(signupAdminRequest.getSurname(), user.getSurname());
        assertEquals(signupAdminRequest.getIdCardNumber(), user.getIdCardNumber());
        assertEquals(signupAdminRequest.getAddressLiving(), user.getAddressLiving());
        assertEquals(signupAdminRequest.getAddressCorrespondence(), user.getAddressCorrespondence());


    }
}
