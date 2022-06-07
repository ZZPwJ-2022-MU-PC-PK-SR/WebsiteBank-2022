package pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.controllers;

import lombok.RequiredArgsConstructor;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.models.User;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.payload.request.ChangeDataRequest;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.payload.response.MessageResponse;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.repository.UserRepository;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.services.CheckCode;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.services.UserDetailsImpl;

import javax.validation.Valid;
import javax.validation.constraints.Null;

@Controller
@RequestMapping("/api/")
@RequiredArgsConstructor
public class OptionController {

    private final CheckCode checkCode;

    private final UserRepository userRepository;

    private final PasswordEncoder encoder;

    @PostMapping("/changedata")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> changeData(@Valid @RequestBody ChangeDataRequest changeDataRequest, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow(() -> new RuntimeException("Fatal authentication error"));
        if(checkCode.checkCode(changeDataRequest.getCode(), userDetails)){
            if(!(changeDataRequest.getEmail() == null)){
                user.setEmail(changeDataRequest.getEmail());
            }
            if(!(changeDataRequest.getName() == null)){
                user.setName(changeDataRequest.getName());
            }
            if(!(changeDataRequest.getSurname()== null)) {
                user.setSurname(changeDataRequest.getSurname());
            }
            if(!(changeDataRequest.getPassword()== null)) {
                user.setPassword(encoder.encode(changeDataRequest.getPassword()));
            }
            if (!(changeDataRequest.getAddressLiving()== null)){
                user.setAddressLiving(changeDataRequest.getAddressLiving());
            }
            if (!(changeDataRequest.getAddressCorrespondence()== null)){
                user.setAddressCorrespondence(changeDataRequest.getAddressCorrespondence());
            }
            userRepository.save(user);
            return ResponseEntity.ok(new MessageResponse("User information has been correctly changed!"));
        }
        return ResponseEntity.badRequest().body(new MessageResponse("Bad authorization code!"));
    }

    @GetMapping("/getuseradata")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getData(@AuthenticationPrincipal UserDetailsImpl userDetails) throws JSONException {
        JSONObject resp = new JSONObject();
        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow(() -> new RuntimeException("Fatal authentication error"));
        resp.put("Name", user.getName());
        resp.put("Surname", user.getSurname());
        resp.put("Email", user.getEmail());
        resp.put("Address living", user.getAddressLiving());
        resp.put("Address correspondence", user.getAddressCorrespondence());
        return ResponseEntity.ok(resp.toString());
    }

}
