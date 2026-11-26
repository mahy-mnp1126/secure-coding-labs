package labs.m6;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Support staff change a customer's address. The support tool also sends the
 * customer's e-mail and SIN, which it uses to verify the caller's identity.
 */
@RestController
class AccountController {
    private final AccountService accounts;

    AccountController(AccountService accounts) {
        this.accounts = accounts;
    }

    @PostMapping("/api/v1/customers/{customerId}/address")
    @PreAuthorize("hasRole('SUPPORT')")
    ResponseEntity<Void> changeAddress(@PathVariable String customerId,
                                       @RequestBody AddressChangeRequest body,
                                       @AuthenticationPrincipal Jwt caller) {
        accounts.changeAddress(caller.getTokenValue(),
                new AddressChange(customerId, body.email(), body.sin(), body.newAddress()));
        return ResponseEntity.noContent().build();
    }

    record AddressChangeRequest(String email, String sin, String newAddress) {}
}
