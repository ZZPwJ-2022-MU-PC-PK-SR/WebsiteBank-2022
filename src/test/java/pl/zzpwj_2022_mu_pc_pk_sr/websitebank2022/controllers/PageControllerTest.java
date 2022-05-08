package pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.controllers;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class PageControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private PageController pageController;



    @Test
    public void shouldNotAllowAccessToUnauthorizedUser() throws Exception{
        mockMvc.perform(get("/api/logged/user")).andExpect(status().isUnauthorized());
    }

    @Test
    public void shouldNotAllowAccessToUnauthorizedAdmin() throws Exception{
        mockMvc.perform(get("/api/logged/admin")).andExpect(status().isUnauthorized());
    }

    @Test
    public void shouldAllowAccessToAuthorizedUser() throws Exception{
        mockMvc.perform(get("/api/logged/user").with(user("user").roles("USER"))).andExpect(status().isOk());
    }

    @Test
    public void shouldAllowAccessToAuthorizedAdmin() throws Exception{
        mockMvc.perform(get("/api/logged/admin").with(user("admin").roles("ADMIN"))).andExpect(status().isOk());
    }

    @Test
    public void shouldAllowAccessToUserPageToAuthorizedAdmin() throws Exception{
        mockMvc.perform(get("/api/logged/user").with(user("admin").roles("ADMIN"))).andExpect(status().isOk());
    }

    @Test
    public void shouldNotAllowAccessToAdminPageToAuthorizedUser() throws Exception{
        mockMvc.perform(get("/api/logged/admin").with(user("user").roles("USER"))).andExpect(status().isForbidden());
    }



}