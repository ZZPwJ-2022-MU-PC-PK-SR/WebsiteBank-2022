package pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.TestingAuthenticationProvider;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.models.*;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.payload.request.LoginRequest;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.payload.request.SingupRequest;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.repository.*;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.security.WebSecurityConfig;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.security.jwt.JwtUtils;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.services.UserDetailsImpl;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@SpringBootTest
@Import(WebSecurityConfig.class)
public class AuthControllerTest {

    @Mock
    private CardRepository cardRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private BankAccountTypeRepository bankAccountTypeRepository;
    @Mock
    private BankAccountRepository bankAccountRepository;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtUtils jwtUtils;
    @InjectMocks
    private AuthController authController;


    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
       mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }


    @Test
    public void singInTest() throws Exception {
        String username = "jonras1234567";
        String password = "password";
        String email = "jonras@wp.pl";
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setPassword(password);
        loginRequest.setUsername(username);
        User user = new User(username, email, password);
        Role role = new Role();
        role.setId(1);
        role.setName(EnumRole.ROLE_USER);
        user.setRoles(new HashSet<>(Arrays.asList(role)));

        UserDetailsImpl userDetails = UserDetailsImpl.build(user);

        String json  = new ObjectMapper().writeValueAsString(loginRequest);
        when(authenticationManager.authenticate(any())).thenReturn(new TestingAuthenticationProvider().authenticate(new TestingAuthenticationToken(userDetails, "password", EnumRole.ROLE_USER.toString())));
        when(jwtUtils.generateJwtToken(any())).thenReturn("jwt_token_wazny");
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/singin").content(json).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.ALL_VALUE)).andExpect(status().isOk()).andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains("\"token\":\"jwt_token_wazny\"")));
    }


    @Test
    public void singupTest() throws Exception {
        SingupRequest singupRequest = new SingupRequest();
        singupRequest.setEmail("jonras@wp.pl");
        singupRequest.setUsername("jonras1234567");
        singupRequest.setRole(new HashSet<>(Arrays.asList("user")));
        singupRequest.setPassword("password");
        singupRequest.setName("jonras1234567");
        singupRequest.setSurname("jonras1234567");
        singupRequest.setPersonalId("jonras1234567");
        singupRequest.setIdCardNumber("jonras1234567");
        singupRequest.setAddressLiving("jonras1234567");
        singupRequest.setAddressCorrespondence("jonras1234567");
        Role role = new Role();
        role.setId(1);
        role.setName(EnumRole.ROLE_USER);
        BankAccountType bankAccountType = new BankAccountType(3,3,3,"cos");
        bankAccountType.setId(1L);
        User user = new User(singupRequest.getUsername(),singupRequest.getEmail(),singupRequest.getPassword(),singupRequest.getName(),singupRequest.getSurname(),singupRequest.getPersonalId(),singupRequest.getIdCardNumber(),singupRequest.getAddressLiving(),singupRequest.getAddressCorrespondence());
        user.setId(1L);
        user.setBoar("xd");
        BankAccount bankAccount = new BankAccount(bankAccountType,user,5000,"12345555555555555555555555");
        when(bankAccountTypeRepository.findById(any())).thenReturn(Optional.of(bankAccountType));
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(roleRepository.findByName(any())).thenReturn(Optional.of(role));
        when(userRepository.save(any())).thenReturn(new User());
        when(passwordEncoder.encode(anyString())).thenReturn("password");
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(cardRepository.save(any())).thenReturn(new Cards());
        String json  = new ObjectMapper().writeValueAsString(singupRequest);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/singup").content(json).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.ALL_VALUE)).andExpect(status().isOk());
    }

    @Test
    public void singupReturnEmailAlreadyExistTest() throws Exception {
        SingupRequest singupRequest = new SingupRequest();
        singupRequest.setEmail("jonras@wp.pl");
        singupRequest.setUsername("jonras1234567");
        singupRequest.setRole(new HashSet<>(Arrays.asList("user")));
        singupRequest.setPassword("password");
        singupRequest.setName("macieeee");
        singupRequest.setSurname("sdjasda");
        singupRequest.setPersonalId("sdasaasda");
        singupRequest.setIdCardNumber("sdasdasda");
        singupRequest.setAddressLiving("sdasdadasd");
        singupRequest.setAddressCorrespondence("sdadsadasdawddq");



        Role role = new Role();
        role.setId(1);
        role.setName(EnumRole.ROLE_USER);
        when(userRepository.existsByEmail(anyString())).thenReturn(true);
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(roleRepository.findByName(any())).thenReturn(Optional.of(role));
        when(userRepository.save(any())).thenReturn(new User());
        when(passwordEncoder.encode(anyString())).thenReturn("password");
        String json  = new ObjectMapper().writeValueAsString(singupRequest);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/singup").content(json).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.ALL_VALUE)).andExpect(result -> assertEquals("{\"message\":\"Error: Email is already in use!\"}", result.getResponse().getContentAsString()));
    }

    @Test
    public void singupReturnUsernameAlreadyExistTest() throws Exception {
        SingupRequest singupRequest = new SingupRequest();
        singupRequest.setEmail("jonras@wp.pl");
        singupRequest.setUsername("jonras1234567");
        singupRequest.setRole(new HashSet<>(Arrays.asList("user")));
        singupRequest.setPassword("password");
        singupRequest.setName("macieeee");
        singupRequest.setSurname("dadw");
        singupRequest.setPersonalId("sda");
        singupRequest.setIdCardNumber("xxzczv");
        singupRequest.setAddressLiving("xcxve");
        singupRequest.setAddressCorrespondence("gebsafgg");
        Role role = new Role();
        role.setId(1);
        role.setName(EnumRole.ROLE_USER);

        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByUsername(anyString())).thenReturn(true);
        when(roleRepository.findByName(any())).thenReturn(Optional.of(role));
        when(userRepository.save(any())).thenReturn(new User());
        when(passwordEncoder.encode(anyString())).thenReturn("password");
        String json  = new ObjectMapper().writeValueAsString(singupRequest);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/singup").content(json).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.ALL_VALUE)).andExpect(status().is4xxClientError()).andExpect(result -> assertEquals("{\"message\":\"Error: Username is already taken!\"}", result.getResponse().getContentAsString()));
    }





}