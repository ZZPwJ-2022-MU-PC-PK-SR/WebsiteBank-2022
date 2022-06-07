package pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.models.AuthorizationCode;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.models.User;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.payload.request.CodeValidateRequest;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.payload.response.BooleanResponse;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.repository.AuthorizationCodeRepository;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.repository.UserRepository;

import javax.validation.Valid;

@Service
public class CheckCode {

    @Autowired
    UserRepository userRepository;
    @Autowired
    AuthorizationCodeRepository authorizationCodeRepository;

    public Boolean checkCode(String code, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow(() -> new RuntimeException("No user with such username found"));
        boolean response = authorizationCodeRepository.existsByUserAndActiveIsTrue(user);
        if(response) {
            AuthorizationCode validCode = authorizationCodeRepository.findTopByActiveTrueAndUserOrderByOrderNo(user);
            if(validCode.getCode().equals(code)) {
                validCode.setActive(false);
                authorizationCodeRepository.save(validCode);
            } else {
                response=false;
            }
        }
        return response;
    }
}
