package labs;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** The app starts, the lab data loads and the security baseline behaves. Runs in every build. */
@SpringBootTest
@AutoConfigureMockMvc
class ApplicationSmokeTest {
    @Autowired MockMvc mvc;

    @Test
    void healthIsPublic() throws Exception {
        mvc.perform(get("/actuator/health")).andExpect(status().isOk());
    }

    @Test
    void customerReadsOwnInvoiceButNotSomeoneElses() throws Exception {
        mvc.perform(get("/api/v1/invoices/41").with(LabUsers.customer(LabUsers.ALICE)))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.description").value("Alice - October services"));
        mvc.perform(get("/api/v1/invoices/42").with(LabUsers.customer(LabUsers.ALICE)))
           .andExpect(status().isNotFound());
    }

    @Test
    void adminEndpointNeedsAdminRole() throws Exception {
        mvc.perform(get("/api/v1/admin/stats").with(LabUsers.customer(LabUsers.ALICE)))
           .andExpect(status().isForbidden());
    }

    @Test
    void unknownPathIsA404NotA500() throws Exception {
        mvc.perform(get("/api/v1/does-not-exist").with(jwt()))
           .andExpect(status().isNotFound());
    }
}
