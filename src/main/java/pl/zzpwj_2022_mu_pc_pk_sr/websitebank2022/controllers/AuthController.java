package pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.models.*;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.payload.request.LoginRequest;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.payload.request.SingupRequest;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.payload.response.JwtResponse;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.payload.response.MessageResponse;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.repository.BankAccountRepository;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.repository.BankAccountTypeRepository;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.repository.RoleRepository;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.repository.UserRepository;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.security.WebSecurityConfig;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.security.jwt.JwtUtils;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.services.UserDetailsImpl;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

//@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
@Import(WebSecurityConfig.class)
public class AuthController {
    @Autowired
    BankAccountTypeRepository bankAccountTypeRepository;
    @Autowired
    BankAccountRepository bankAccountRepository;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    UserRepository userRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    PasswordEncoder encoder;
    @Autowired
    JwtUtils jwtUtils;
    @PostMapping(path="/singin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        return ResponseEntity.ok(new JwtResponse(jwt));
    }

    @PostMapping("/singup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SingupRequest singupRequest) {
        if( userRepository.existsByUsername(singupRequest.getUsername())){
            return ResponseEntity
                    .badRequest().body(new MessageResponse("Error: Username is already taken!"));
        }
        if (userRepository.existsByEmail(singupRequest.getEmail())){
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
        }

        User user = new User(singupRequest.getUsername(), singupRequest.getEmail(), encoder.encode(singupRequest.getPassword()), singupRequest.getName(),singupRequest.getSurname(),singupRequest.getPersonalId(),singupRequest.getIdCardNumber(),singupRequest.getAddressLiving(),singupRequest.getAddressCorrespondence());
        Set<String> strRoles = singupRequest.getRole();
        Set<Role> roles = new HashSet<>();
        if(strRoles == null) {
            Role userRole = roleRepository.findByName(EnumRole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found"));
            roles.add(userRole);
        } else {
            strRoles.forEach(role->{
                if ("admin".equals(role)) {
                    Role adminRole = roleRepository.findByName(EnumRole.ROLE_ADMIN)
                            .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                    roles.add(adminRole);
                } else {
                    Role userRole = roleRepository.findByName(EnumRole.ROLE_USER)
                            .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                    roles.add(userRole);
                }
            });
        }
        BankAccountType bankAccountType = bankAccountTypeRepository.findById(1L).get();
        user.setRoles(roles);
        userRepository.save(user);
        user = userRepository.findByEmail(user.getEmail()).get();
        String accountNumber = "";
        for(int i = 0 ; i < 26; i++){
            int rand = (new Random().nextInt(10));
            accountNumber += String.valueOf(rand);

        }
        BankAccount bankAccount = new BankAccount(bankAccountType,user,1000, accountNumber);
        bankAccountRepository.save(bankAccount);
        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

}
