package labs;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

/** Test users. The ids match keycloak/realm-labs.json and src/main/resources/data.sql. */
public final class LabUsers {
    public static final String ALICE = "a11ce000-0000-4000-8000-000000000001";
    public static final String BOB = "b0b00000-0000-4000-8000-000000000002";
    public static final String SUPPORT = "5a990000-0000-4000-8000-000000000003";
    public static final String ADMIN = "ad000000-0000-4000-8000-000000000004";

    private LabUsers() {
    }

    public static JwtRequestPostProcessor customer(String subject) {
        return jwt().jwt(j -> j.subject(subject))
                .authorities(new SimpleGrantedAuthority("ROLE_CUSTOMER"));
    }

    public static JwtRequestPostProcessor support() {
        return jwt().jwt(j -> j.subject(SUPPORT))
                .authorities(new SimpleGrantedAuthority("ROLE_SUPPORT"));
    }
}
