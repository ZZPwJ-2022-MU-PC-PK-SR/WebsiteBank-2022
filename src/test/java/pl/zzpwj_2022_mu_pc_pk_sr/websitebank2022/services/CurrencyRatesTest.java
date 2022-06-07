package pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class CurrencyRatesTest {
    @Autowired
    private CurrencyRates currencyRates;

    @Test
    public void falseCurrencyThrowsAnError() {
        assertThrows(RuntimeException.class, () -> {
            currencyRates.getRate("FFF");
        });
    }

    @Test
    public void euroCurrencyHigherThanPLN() {
        assertTrue(1.0<currencyRates.getRate("EUR"));
    }

    @Test
    public void russianRubbleLessThanPLN() {
        assertTrue(1.0>currencyRates.getRate("RUB"));
    }
}
