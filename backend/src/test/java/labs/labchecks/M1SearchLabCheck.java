package labs.labchecks;

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
 * Module 1, exercise 1.8 (Tasks 2 and 3). These checks FAIL on the seed and PASS once fixed.
 * Run with:  ./lab-check.sh m1
 */
@Tag("lab")
@Tag("m1")
@SpringBootTest
@AutoConfigureMockMvc
class M1SearchLabCheck {
    @Autowired MockMvc mvc;

    @Test
    @DisplayName("Task 2: an injection payload is treated as plain search text")
    void injectionPayloadReturnsNothing() throws Exception {
        MvcResult r = mvc.perform(get("/api/v1/products")
                .param("q", "x%' OR 1=1 -- ")
                .with(LabUsers.customer(LabUsers.ALICE))).andReturn();
        assertThat(r.getResponse().getStatus()).as("status for the injection payload").isEqualTo(200);
        assertThat(r.getResponse().getContentAsString())
                .as("No product is named \"x%' OR 1=1 -- \", so the result must be empty. "
                    + "If you see every product, the input is still concatenated into the SQL.")
                .isEqualTo("[]");
    }

    @Test
    @DisplayName("Task 2: a UNION payload cannot read the users table")
    void unionPayloadCannotReadUsers() throws Exception {
        MvcResult r = mvc.perform(get("/api/v1/products")
                .param("q", "x%' UNION SELECT id, username || ':' || password_hash, 0 FROM users --")
                .with(LabUsers.customer(LabUsers.ALICE))).andReturn();
        assertThat(r.getResponse().getContentAsString())
                .as("password hashes leaked through the product search")
                .doesNotContain("LAB.ONLY.FAKE.HASH");
    }

    @Test
    @DisplayName("Task 2: legitimate searches still work, including apostrophes")
    void normalSearchesWork() throws Exception {
        MvcResult lamp = mvc.perform(get("/api/v1/products").param("q", "lamp")
                .with(LabUsers.customer(LabUsers.ALICE))).andReturn();
        assertThat(lamp.getResponse().getContentAsString()).contains("Desk lamp");

        MvcResult apostrophe = mvc.perform(get("/api/v1/products").param("q", "O'Neil")
                .with(LabUsers.customer(LabUsers.ALICE))).andReturn();
        assertThat(apostrophe.getResponse().getStatus())
                .as("searching for O'Neil must not break the SQL")
                .isEqualTo(200);
        assertThat(apostrophe.getResponse().getContentAsString()).contains("O'Neil notebook");
    }

    @Test
    @DisplayName("Task 3: anonymous callers cannot search")
    void anonymousCannotSearch() throws Exception {
        int status = mvc.perform(get("/api/v1/products").param("q", "lamp"))
                .andReturn().getResponse().getStatus();
        assertThat(status).as("anonymous search must be rejected (401 or 403)").isIn(401, 403);
    }
}
