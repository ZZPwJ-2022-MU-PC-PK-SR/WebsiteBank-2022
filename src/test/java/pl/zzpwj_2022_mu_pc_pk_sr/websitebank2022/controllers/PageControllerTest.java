package pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.controllers;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;


import org.junit.jupiter.api.TestInstance;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.mockusers.WithMockCustomAdmin;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.mockusers.WithMockCustomUser;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.models.*;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.repository.TransactionHistoryRepository;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.repository.UserRepository;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.security.WebSecurityConfig;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.services.TransactionHistoryService;

import javax.transaction.Transactional;
import java.sql.SQLOutput;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
@AutoConfigureMockMvc
@SpringBootTest
@Import({ WebSecurityConfig.class})
public class PageControllerTest {


    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;


    @Test
    public void shouldNotAllowAccessToUnauthorizedUser() throws Exception{
        userRepository.findAll();
        mockMvc.perform(get("/api/logged/user")).andExpect(status().isUnauthorized());
    }

    @Test
    public void shouldNotAllowAccessToUnauthorizedAdmin() throws Exception{
        userRepository.findAll();
        mockMvc.perform(get("/api/logged/admin")).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockCustomUser
    public void shouldAllowAccessToAuthorizedUser() throws Exception{
        mockMvc.perform(get("/api/logged/user")).andExpect(status().isOk()).andExpect(result -> assertEquals(result.getResponse().getContentAsString(), "{\"message\":\"usernamefine\"}"));
    }

    @Test
    @WithMockCustomAdmin
    public void shouldAllowAccessToAuthorizedAdmin() throws Exception{
        mockMvc.perform(get("/api/logged/admin")).andExpect(status().isOk());
    }

    @Test
    @WithMockCustomAdmin
    public void shouldAllowAccessToUserPageToAuthorizedAdmin() throws Exception{
        mockMvc.perform(get("/api/logged/user")).andExpect(status().isOk()).andExpect(result -> assertEquals(result.getResponse().getContentAsString(), "{\"message\":\"adminadmin\"}"));
    }

    @Test
    @WithMockCustomUser
    public void shouldNotAllowAccessToAdminPageToAuthorizedUser() throws Exception{
        mockMvc.perform(get("/api/logged/admin")).andExpect(status().isUnauthorized());
    }
    @Test
    public void shouldNotAllowAccessToDashboardToAuthorizedUser() throws Exception{
        mockMvc.perform(get("/api/logged/dashboard")).andExpect(status().isUnauthorized());
    }
    @Test
    public void shouldNotAllowAccessToDashboardUnauthorizedUser() throws Exception{
        mockMvc.perform(get("/api/logged/dashboard")).andExpect(status().isUnauthorized());
    }
    @Test
    @WithMockCustomUser
    public void shouldAllowAccessToDashboardToAuthorizedUser() throws Exception{
        mockMvc.perform(get("/api/logged/dashboard")).andExpect(status().isOk());
    }
    @Test
    @WithMockCustomAdmin
    public void shouldAllowAccessToDashboardToAuthorizedAdmin() throws Exception{
        mockMvc.perform(get("/api/logged/dashboard")).andExpect(status().isOk());
    }

    @Test
    @WithMockCustomAdmin
    public void shouldAllowAccessToDashboardToAuthorizedAdminCheckMessage() throws Exception{
        mockMvc.perform(get("/api/logged/dashboard")).andExpect(status().isOk()).andExpect(result -> assertEquals(result.getResponse().getContentAsString(), "{\"message\":\"adminadmin\\n Imie : null\\n Nazwisko : null\\n Numer karty : null\\n\"}"));
    }

}