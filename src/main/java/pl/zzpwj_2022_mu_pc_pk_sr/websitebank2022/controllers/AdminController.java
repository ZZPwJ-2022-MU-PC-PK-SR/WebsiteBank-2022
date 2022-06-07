package pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.models.*;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.payload.request.SignupAdminRequest;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.payload.request.SingupRequest;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.payload.response.MessageResponse;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.repository.AdminPasswordRepository;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.repository.RoleRepository;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.repository.UserRepository;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.security.WebSecurityConfig;

import javax.validation.Valid;
import java.util.*;

@RestController
@RequestMapping("/api/admin")
@Import(WebSecurityConfig.class)
@RequiredArgsConstructor
public class AdminController {
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final RoleRepository roleRepository;
    private final AdminPasswordRepository adminPasswordRepository;

    @PostMapping("/signup")
    public ResponseEntity<?> registerAdmin(@Valid @RequestBody SignupAdminRequest signupRequest) {
        System.out.println(signupRequest.getPasswordAdmin());
        List<PasswordAdmin> passwordAdmins = adminPasswordRepository.findAll();
        for (PasswordAdmin passwordAdmin : passwordAdmins){
            if(encoder.matches(signupRequest.getPasswordAdmin(), passwordAdmin.getPassword())){

                if( userRepository.existsByUsername(signupRequest.getUsername())){
                    return ResponseEntity
                            .badRequest().body(new MessageResponse("Error: Username is already taken!"));
                }
                if (userRepository.existsByEmail(signupRequest.getEmail())){
                    return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
                }
                Set<String> strRoles = signupRequest.getRole();
                if(strRoles == null && strRoles.isEmpty() ){
                    return ResponseEntity.badRequest().body(new MessageResponse("Error: No role indicated!"));
                }

                User user = new User(signupRequest.getUsername(), signupRequest.getEmail(), encoder.encode(signupRequest.getPassword()), signupRequest.getName(),signupRequest.getSurname(),signupRequest.getPersonalId(),signupRequest.getIdCardNumber(),signupRequest.getAddressLiving(),signupRequest.getAddressCorrespondence());

                Set<Role> roles = new HashSet<>();
                if(strRoles.contains("admin")) {
                    Role userRole = roleRepository.findByName(EnumRole.ROLE_ADMIN)
                            .orElseThrow(() -> new RuntimeException("Error: Role is not found"));
                    roles.add(userRole);
                } else {
                    return ResponseEntity.badRequest().body(new MessageResponse("Error: Bad role indicated!"));
                }
                user.setRoles(roles);
                userRepository.save(user);
                return ResponseEntity.ok(new MessageResponse("Admin registered successfully!"));
            }
        }
        return ResponseEntity
                .badRequest().body(new MessageResponse("Error: Bad admin password!"));
    }
}
