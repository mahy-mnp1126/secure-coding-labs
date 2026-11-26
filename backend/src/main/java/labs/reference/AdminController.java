package labs.reference;

import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/** Admin-only endpoint (SecurityConfig restricts /api/v1/admin/** to ROLE_ADMIN). */
@RestController
class AdminController {
    private final JdbcTemplate jdbc;

    AdminController(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @GetMapping("/api/v1/admin/stats")
    Map<String, Object> stats() {
        Integer users = jdbc.queryForObject("SELECT COUNT(*) FROM users", Integer.class);
        Integer invoices = jdbc.queryForObject("SELECT COUNT(*) FROM invoices", Integer.class);
        return Map.of("users", users, "invoices", invoices);
    }
}
