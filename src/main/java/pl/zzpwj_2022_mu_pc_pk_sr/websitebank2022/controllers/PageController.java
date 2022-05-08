package pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.services.UserDetailsImpl;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/logged")
public class PageController {
    @GetMapping("/user")
    @PreAuthorize(value = "hasRole('USER') or hasRole('ADMIN')")
    public String userAccess(HttpServletRequest request) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        return userDetails.getUsername();
    }

    @GetMapping("/admin")
    @PreAuthorize(value = "hasRole('ADMIN')")
    public String adminAccess() {
        return "Admin Content";
    }

}
