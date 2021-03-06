package pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.controllers;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.coyote.Response;
import org.aspectj.bridge.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.models.AuthorizationCode;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.models.User;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.payload.request.CodeGenerateRequest;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.payload.request.CodeValidateRequest;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.payload.response.BooleanResponse;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.payload.response.CodesResponse;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.payload.response.MessageResponse;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.repository.AuthorizationCodeRepository;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.repository.UserRepository;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.services.UserDetailsImpl;

import javax.validation.Valid;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.util.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@Controller
@RequestMapping("/api/authcodes")
@Log4j2
@RequiredArgsConstructor
public class AuthorizationCodeController {

    private final UserRepository userRepository;

    private final AuthorizationCodeRepository authorizationCodeRepository;

    @PostMapping("/generate_codes")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> generateCodes(@Valid @RequestBody CodeGenerateRequest request) throws NoSuchAlgorithmException, RuntimeException {
        User user = userRepository.findByUsername(request.getUsername()).orElseThrow(() -> new RuntimeException("No user with such username found"));
        if (authorizationCodeRepository.existsByUserAndActiveIsTrue(user)) {
            return ResponseEntity.badRequest().body(new MessageResponse("User has active codes"));
        } else {
            List<String> response = new ArrayList<>();
            for(int i=0;i<50;i++) {
                AuthorizationCode code = new AuthorizationCode(user,i);
                response.add(code.getCode());
                authorizationCodeRepository.save(code);
            }
            return ResponseEntity.ok().body(new CodesResponse(response));
        }
    }

    @PostMapping("/compromise_codes")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> compromiseCodes(@Valid @RequestBody CodeGenerateRequest request) {
        User user = userRepository.findByUsername(request.getUsername()).orElseThrow(() -> new RuntimeException("No user with such username found"));
        if (authorizationCodeRepository.existsByUserAndActiveIsTrue(user)) {
            Set<AuthorizationCode> codes = authorizationCodeRepository.findAuthorizationCodesByActiveTrueAndUser(user);
            codes.forEach((k) -> {
                k.setActive(false);
                authorizationCodeRepository.save(k);
            });
            return ResponseEntity.ok().body(new MessageResponse("Codes sucessfully compromised"));
        } else {
            return ResponseEntity.badRequest().body(new MessageResponse("User has no active codes"));
        }
    }

}
