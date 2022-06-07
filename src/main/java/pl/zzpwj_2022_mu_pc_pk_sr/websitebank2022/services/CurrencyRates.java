package pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.services;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Map;

@Service
public class CurrencyRates {

    public Double getRate(String currencyCode) {
        RestTemplate restTemplate = new RestTemplate();
        String tableAUrl = "http://api.nbp.pl/api/exchangerates/rates/A/" + currencyCode + "/?format=json";
        Double rate;
        try{
            ResponseEntity<Map> response = restTemplate.getForEntity(tableAUrl,Map.class);
            ArrayList<Map> rates = (ArrayList<Map>) response.getBody().get("rates");
            rate = (Double) rates.get(0).get("mid");
        } catch(HttpClientErrorException.NotFound ex) {
            String tableBUrl = "http://api.nbp.pl/api/exchangerates/rates/B/" + currencyCode + "/?format=json";
            try {
                ResponseEntity<Map> response = restTemplate.getForEntity(tableBUrl, Map.class);
                ArrayList<Map> rates = (ArrayList<Map>) response.getBody().get("rates");
                rate = (Double) rates.get(0).get("mid");
            } catch(HttpClientErrorException.NotFound ex2) {
                throw new RuntimeException("Error: currency not found");
            }
        }
        return rate;
    }

}
