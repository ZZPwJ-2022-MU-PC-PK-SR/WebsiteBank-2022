package pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.controllers;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;


import org.springframework.beans.factory.annotation.Autowired;
import static org.mockito.Mockito.when;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.mockusers.WithMockCustomAdmin;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.mockusers.WithMockCustomUser;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.models.*;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.payload.request.LoginRequest;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.repository.*;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.security.WebSecurityConfig;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.services.CheckCode;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.services.TransactionHistoryService;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.services.UserDetailsImpl;

import javax.transaction.Transactional;
//import java.sql.SQLOutput;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
@AutoConfigureMockMvc
@SpringBootTest
public class PageControllerTest {
    ObjectMapper objectMapper = new ObjectMapper();


    @Autowired
    private MockMvc mockMvc;





    @Test
    public void shouldNotAllowAccessToUnauthorizedUser() throws Exception{
        mockMvc.perform(get("/api/logged/user")).andExpect(status().isUnauthorized());
    }

    @Test
    public void shouldNotAllowAccessToUnauthorizedAdmin() throws Exception{
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
        mockMvc.perform(get("/api/logged/dashboard")).andExpect(status().isOk()).andExpect(result -> assertEquals(result.getResponse().getContentAsString(), "[]"));
    }



    @Test
    @WithMockCustomUser
    public void shouldAllowAccessToAccountPercentageMoneyToAuthorizedUser() throws Exception{
        mockMvc.perform(get("/api/logged/account_percentage_money")).andExpect(status().isBadRequest());
    }
    @Test
    @WithMockCustomAdmin
    public void shouldAllowAccessToAccountPercentageMoneyToAuthorizedAdmin() throws Exception{
        mockMvc.perform(get("/api/logged/account_percentage_money")).andExpect(status().isBadRequest());
    }

    @Test
    public void shouldNotAllowAccessToBlockTheCardToAuthorizedUser() throws Exception{
        mockMvc.perform(get("/api/logged/block_the_card")).andExpect(status().isBadRequest());
    }

    @Test
    @WithMockCustomAdmin
    public void shouldAllowAccessToBlockTheCardToAuthorizedAdmin() throws Exception{
        mockMvc.perform(get("/api/logged/block_the_card")).andExpect(status().isBadRequest());
    }
    @Test
    @WithMockCustomUser
    public void shouldAllowAccessToRequestNewCardToAuthorizedUser() throws Exception{
        mockMvc.perform(get("/api/logged/request_new_card")).andExpect(status().isBadRequest());
    }
    @Test
    @WithMockCustomAdmin
    public void shouldAllowAccessToRequestNewCardToAuthorizedAdmin() throws Exception{
        mockMvc.perform(get("/api/logged/request_new_card")).andExpect(status().isBadRequest());
    }






}