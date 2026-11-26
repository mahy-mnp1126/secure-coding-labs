package labs.labchecks;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import labs.LabUsers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

/**
 * Module 1, exercise 1.8, Task 4 (take-home): SSRF.
 * Run with:  ./lab-check.sh m1-ssrf
 */
@Tag("lab")
@Tag("m1-ssrf")
@SpringBootTest
@AutoConfigureMockMvc
class M1SsrfLabCheck {
    static LocalHttpServer internalService;

    @Autowired MockMvc mvc;

    @BeforeAll
    static void startInternalService() throws Exception {
        internalService = new LocalHttpServer(
                () -> new LocalHttpServer.Reply(200, "{\"secret\":\"INTERNAL-ONLY-DATA\"}", 0));
    }

    @AfterAll
    static void stop() {
        internalService.close();
    }

    @Test
    @DisplayName("Task 4: the server refuses to fetch an internal address")
    void internalAddressIsRefused() throws Exception {
        MvcResult r = mvc.perform(get("/api/v1/partner-price")
                .param("url", internalService.baseUrl() + "/admin")
                .with(LabUsers.customer(LabUsers.ALICE))).andReturn();
        assertThat(r.getResponse().getContentAsString())
                .as("the response must not contain data from an internal service")
                .doesNotContain("INTERNAL-ONLY-DATA");
        assertThat(r.getResponse().getStatus()).as("status").isNotEqualTo(200);
        assertThat(internalService.hits())
                .as("the internal service must never be called")
                .isZero();
    }
}
