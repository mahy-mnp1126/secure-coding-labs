package labs.m6;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

// ============================================================================
// LAB SEED: DELIBERATELY VULNERABLE (Module 6, exercise 6.4)
// Flaws: logs the whole request record (a record's toString prints every
// field), e-mail, SIN and bearer token on failure; no audit event.
// Do not copy this code into real projects.
// ============================================================================
@Service
class AccountService {
    private static final Logger log = LoggerFactory.getLogger(AccountService.class);
    private final JdbcTemplate jdbc;

    AccountService(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public void changeAddress(String bearerToken, AddressChange req) {
        log.info("Address change request: " + req);
        try {
            jdbc.update("UPDATE customers SET address = ? WHERE id = ?",
                    req.newAddress(), req.customerId());
        } catch (DataAccessException e) {
            log.error("Update failed for " + req.email() + " SIN " + req.sin()
                    + " token " + bearerToken, e);
            throw e;
        }
    }
}
