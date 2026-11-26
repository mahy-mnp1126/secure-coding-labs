package labs.labchecks;

import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import labs.LabUsers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

/**
 * Module 7, take-home exercise 7.3. These checks FAIL on the seed and PASS once fixed.
 * Run with:  ./lab-check.sh m7
 */
@Tag("lab")
@Tag("m7")
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class M7ResilienceLabCheck {
    static final LocalHttpServer.Reply OK =
            new LocalHttpServer.Reply(200, "{\"currency\":\"CAD\",\"value\":1.37}", 0);
    static final LocalHttpServer.Reply SLOW =
            new LocalHttpServer.Reply(200, "{\"currency\":\"CAD\",\"value\":9.99}", 8_000);
    static final LocalHttpServer.Reply BROKEN =
            new LocalHttpServer.Reply(500, "{\"error\":\"partner down\"}", 0);

    static final AtomicReference<LocalHttpServer.Reply> mode = new AtomicReference<>(OK);
    static LocalHttpServer partner;

    @BeforeAll
    static void startPartner() throws Exception {
        partner = new LocalHttpServer(mode::get);
    }

    @AfterAll
    static void stopPartner() {
        partner.close();
    }

    @DynamicPropertySource
    static void partnerUrl(DynamicPropertyRegistry registry) {
        registry.add("app.rates.base-url", () -> partner.baseUrl());
    }

    @Autowired MockMvc mvc;

    @Test
    @DisplayName("Timeouts + fallback: a slow partner gets a fast answer from the cached value")
    void slowPartnerFallsBackQuickly() throws Exception {
        mode.set(OK);
        MvcResult first = mvc.perform(get("/api/v1/rates/CAD").with(LabUsers.customer(LabUsers.ALICE)))
                .andReturn();
        assertThat(first.getResponse().getContentAsString()).contains("1.37");

        mode.set(SLOW);
        long started = System.nanoTime();
        MvcResult slow = mvc.perform(get("/api/v1/rates/CAD").with(LabUsers.customer(LabUsers.ALICE)))
                .andReturn();
        long millis = (System.nanoTime() - started) / 1_000_000;
        assertThat(millis).as("response time (ms) while the partner takes 8 s; add read/connect timeouts")
                .isLessThan(5_000);
        assertThat(slow.getResponse().getStatus()).as("status when falling back").isEqualTo(200);
        assertThat(slow.getResponse().getContentAsString())
                .as("the fallback should return the last good (cached) rate")
                .contains("1.37");
    }

    @Test
    @DisplayName("Rate limiter / circuit breaker: a failing partner is not hammered")
    void failingPartnerIsNotHammered() throws Exception {
        mode.set(BROKEN);
        partner.resetHits();
        for (int i = 0; i < 150; i++) {
            mvc.perform(get("/api/v1/rates/CAD").with(LabUsers.customer(LabUsers.ALICE)));
        }
        assertThat(partner.hits())
                .as("calls that reached the failing partner out of 150 requests")
                .isLessThan(120);
    }
}
