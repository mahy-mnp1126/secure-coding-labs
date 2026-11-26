package labs.m7;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
class RatesController {
    private final RatesService rates;

    RatesController(RatesService rates) {
        this.rates = rates;
    }

    @GetMapping("/api/v1/rates/{currency}")
    Rate latest(@PathVariable String currency) {
        return rates.latest(currency);
    }
}
