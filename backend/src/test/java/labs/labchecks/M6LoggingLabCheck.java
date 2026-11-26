package labs.labchecks;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import labs.LabUsers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

/**
 * Module 6, exercise 6.4. These checks FAIL on the seed and PASS once fixed.
 * Run with:  ./lab-check.sh m6
 */
@Tag("lab")
@Tag("m6")
@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(OutputCaptureExtension.class)
class M6LoggingLabCheck {
    static final String TOKEN = "eyJhbGciOiJSUzI1NiJ9.LAB-SECRET-TOKEN.signature";
    static final String EMAIL = "jane.doe@example.com";
    static final String SIN = "046 454 286";

    @Autowired MockMvc mvc;

    private String changeAddress(CapturedOutput output, String newAddress) throws Exception {
        int start = output.getOut().length();
        mvc.perform(post("/api/v1/customers/C-1001/address")
                .with(jwt().jwt(j -> j.subject(LabUsers.SUPPORT).tokenValue(TOKEN))
                           .authorities(new SimpleGrantedAuthority("ROLE_SUPPORT")))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"" + EMAIL + "\",\"sin\":\"" + SIN
                         + "\",\"newAddress\":\"" + newAddress + "\"}"));
        return output.getOut().substring(start);
    }

    @Test
    @DisplayName("Task 1: no SIN, e-mail, token or address in the logs (successful change)")
    void noPersonalInformationOnSuccess(CapturedOutput output) throws Exception {
        String logs = changeAddress(output, "300 New Road, Toronto, ON");
        assertThat(logs).as("SIN in the logs").doesNotContain(SIN);
        assertThat(logs).as("e-mail in the logs").doesNotContain(EMAIL);
        assertThat(logs).as("bearer token in the logs").doesNotContain("LAB-SECRET-TOKEN");
        assertThat(logs).as("home address in the logs").doesNotContain("300 New Road");
    }

    @Test
    @DisplayName("Task 1: no SIN, e-mail or token in the logs (failed change)")
    void noPersonalInformationOnFailure(CapturedOutput output) throws Exception {
        String logs = changeAddress(output, "X".repeat(300));   // too long for the column
        assertThat(logs).as("SIN in the logs").doesNotContain(SIN);
        assertThat(logs).as("e-mail in the logs").doesNotContain(EMAIL);
        assertThat(logs).as("bearer token in the logs").doesNotContain("LAB-SECRET-TOKEN");
    }

    @Test
    @DisplayName("Task 2: an AUDIT event with actor, subject, outcome and correlation id (success)")
    void auditEventOnSuccess(CapturedOutput output) throws Exception {
        String logs = changeAddress(output, "301 New Road, Toronto, ON");
        assertThat(logs).as("expected an audit line like: [cid=<uuid>] AUDIT - "
                            + "event=customer.address.update actor=<id> subject=C-1001 outcome=success")
                .containsPattern("cid=[0-9a-f-]{36}\\].*event=customer\\.address\\.update"
                        + ".*actor=" + LabUsers.SUPPORT + ".*subject=C-1001.*outcome=success");
    }

    @Test
    @DisplayName("Task 2: failures are audited too")
    void auditEventOnFailure(CapturedOutput output) throws Exception {
        String logs = changeAddress(output, "Y".repeat(300));
        assertThat(logs).containsPattern(
                "event=customer\\.address\\.update.*subject=C-1001.*outcome=failure");
    }
}
