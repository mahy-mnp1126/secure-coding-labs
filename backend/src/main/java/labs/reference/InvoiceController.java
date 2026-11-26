package labs.reference;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * REFERENCE (safe) implementation from Section 1.4. Its access-control tests are in
 * src/test/java/labs/reference/InvoiceAccessTest.java.
 */
@RestController
class InvoiceController {
    private final InvoiceRepository invoiceRepository;

    InvoiceController(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    // The lookup is scoped to the authenticated caller. An invoice the caller does
    // not own looks exactly like one that does not exist (404), so ids cannot be probed.
    @GetMapping("/api/v1/invoices/{id}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public Invoice getInvoice(@PathVariable Long id,
                              @AuthenticationPrincipal Jwt caller) {
        return invoiceRepository.findByIdAndOwnerId(id, caller.getSubject())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }
}
