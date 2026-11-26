package labs.m7;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

// ============================================================================
// LAB SEED: DELIBERATELY FRAGILE (Module 7, take-home exercise 7.3)
// Flaws: no timeouts, no rate limit, no circuit breaker, no fallback.
// ============================================================================
@Service
class RatesService {
    private final RestClient client;

    RatesService(@Value("${app.rates.base-url}") String baseUrl) {
        this.client = RestClient.create(baseUrl);
    }

    public Rate latest(String currency) {
        return client.get().uri("/v1/rates/{c}", currency).retrieve().body(Rate.class);
    }
}
