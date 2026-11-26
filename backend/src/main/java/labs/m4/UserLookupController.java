package labs.m4;

import java.sql.SQLException;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
class UserLookupController {
    private final UserLookupService users;

    UserLookupController(UserLookupService users) {
        this.users = users;
    }

    @GetMapping("/api/v1/users/lookup")
    @PreAuthorize("hasAnyRole('SUPPORT', 'ADMIN')")
    UserSummary lookup(@RequestParam String username) throws SQLException {
        return users.findUser(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }
}
