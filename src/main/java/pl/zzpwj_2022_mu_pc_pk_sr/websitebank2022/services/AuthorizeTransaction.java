package pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.services;

import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.Check;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.models.TransactionType;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthorizeTransaction {
    private final PasswordEncoder encoder;
    private final CheckCode checkCode;

    public Boolean authorizeTransaction(TransactionType type, UserDetailsImpl userDetails, String authorizationData) {
        switch(type.getName()) {
            case CARD: {
                return true;
            }
            case DZIK: {
                return getCurrentUserDzik(userDetails).equals(authorizationData);
            }
            case TRANSFER: {
                return checkCode.checkCode(authorizationData,userDetails);
            }
            default:
                throw new IllegalStateException("Unexpected value: " + type.getName());
        }
    }

    public String getCurrentUserDzik(UserDetailsImpl userDetails) {
        String dateAndHourPart = new SimpleDateFormat("!y#M%d&H(").format(Calendar.getInstance().getTime());
        String minutePart = new SimpleDateFormat("m").format(Calendar.getInstance().getTime());
        minutePart = String.valueOf((Integer.parseInt(minutePart)%10)/2);
        String hashString = userDetails.getUsername()+"@"+dateAndHourPart+"$"+minutePart+"^"+userDetails.getPersonalId();
        Random randomGenerator = new Random(hashString.hashCode());
        StringBuilder dzik = new StringBuilder();
        for(int i=0;i<6;i++) {
            dzik.append(randomGenerator.nextInt(10));
        }

        return dzik.toString();
    }
}
