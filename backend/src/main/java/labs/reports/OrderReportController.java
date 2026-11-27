package labs.reports;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// SEEDED ALERT 1 of 3 (Module 4, Task 5): a real SQL injection.
// The sort column comes straight from the request.
@RestController
class OrderReportController {
    private final JdbcTemplate jdbc;

    OrderReportController(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @GetMapping("/api/v1/admin/reports/products")
    @PreAuthorize("hasRole('ADMIN')")
    List<Map<String, Object>> products(@RequestParam(defaultValue = "name") String sort) {
        return jdbc.queryForList("SELECT id, name, price FROM products ORDER BY " + sort);
    }
}
