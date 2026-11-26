package labs.m1;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

// ============================================================================
// LAB SEED: DELIBERATELY VULNERABLE (Module 1, exercise 1.8)
// Flaws: SQL injection (LIKE clause), no authentication (the paths are
// permitAll in labs.config.SecurityConfig), and SSRF through the partner-price
// lookup. Do not copy this code into real projects.
// ============================================================================
@RestController
class SearchController {
    private final JdbcTemplate jdbc;
    private final RestClient restClient = RestClient.create();

    SearchController(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @GetMapping("/api/v1/products")
    public List<Map<String, Object>> search(@RequestParam String q) {
        return jdbc.queryForList(
                "SELECT id, name, price FROM products WHERE name LIKE '%" + q + "%'");
    }

    @GetMapping("/api/v1/partner-price")
    public String partnerPrice(@RequestParam String url) {
        return restClient.get().uri(url).retrieve().body(String.class);
    }
}
