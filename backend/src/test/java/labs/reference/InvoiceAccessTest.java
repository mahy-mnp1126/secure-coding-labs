package labs.reference;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import labs.common.ApiExceptionHandler;
import labs.config.SecurityConfig;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Access-control regression tests (instructor workbook, Module 3 "make it stay fixed"):
 * the exploit becomes a test that must stay green. Try deleting the ownership filter in
 * InvoiceController and run this test again.
 */
@WebMvcTest(InvoiceController.class)
@Import({SecurityConfig.class, ApiExceptionHandler.class})
class InvoiceAccessTest {
    @Autowired MockMvc mvc;
    @MockitoBean InvoiceRepository invoices;
    @MockitoBean JwtDecoder jwtDecoder;

    @Test
    void customerCanReadTheirOwnInvoice() throws Exception {
        when(invoices.findByIdAndOwnerId(41L, "alice"))
                .thenReturn(Optional.of(new Invoice(41L, "alice", new BigDecimal("120.00"), "October")));
        mvc.perform(get("/api/v1/invoices/41")
                .with(jwt().jwt(j -> j.subject("alice"))
                           .authorities(new SimpleGrantedAuthority("ROLE_CUSTOMER"))))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.id").value(41));
    }

    @Test
    void customerCannotReadSomeoneElsesInvoice() throws Exception {
        when(invoices.findByIdAndOwnerId(42L, "alice")).thenReturn(Optional.empty());
        mvc.perform(get("/api/v1/invoices/42")
                .with(jwt().jwt(j -> j.subject("alice"))
                           .authorities(new SimpleGrantedAuthority("ROLE_CUSTOMER"))))
           .andExpect(status().isNotFound());
    }

    @Test
    void wrongRoleIsForbidden() throws Exception {
        mvc.perform(get("/api/v1/invoices/42")
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_SUPPORT"))))
           .andExpect(status().isForbidden());
    }

    @Test
    void anonymousIsRejected() throws Exception {
        mvc.perform(get("/api/v1/invoices/42"))
           .andExpect(status().isUnauthorized());
    }
}
