package pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.services;

import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.Check;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.models.Cards;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.models.TransactionType;
import pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.repository.CardRepository;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthorizeTransaction {
    private final PasswordEncoder encoder;
    private final CheckCode checkCode;
    private final CardRepository cardRepository;

    public Boolean authorizeTransaction(TransactionType type, UserDetailsImpl userDetails, String authorizationData) {
        switch(type.getName()) {
            case CARD: {
                String[] authorizationDataSplit = authorizationData.split("#");
                String encoded = encoder.encode(authorizationDataSplit[0]);
                List<Cards> cardList = cardRepository.findAll();
                Cards card=null;
                for(Cards cardInList : cardList) {
                    if(encoder.matches(authorizationDataSplit[0],cardInList.getCardNumber())) {
                        card = cardInList;
                        break;
                    }
                }
                if(card==null) {
                    return false;
                }
                if(card.getStatus().equals("inActive")) {
                    return false;
                }
                if(card.getAccessDate().before(new Date())) {
                    return false;
                }
                if(!card.getBankAccountId().equals(authorizationDataSplit[1])) {
                    return false;
                }
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
