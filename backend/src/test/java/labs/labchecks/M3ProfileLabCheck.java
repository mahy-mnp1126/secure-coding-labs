package labs.labchecks;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import javax.imageio.ImageIO;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import labs.LabUsers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

/**
 * Module 3, exercise 3.7. These checks FAIL on the seed and PASS once fixed.
 * Run with:  ./lab-check.sh m3
 *
 * <p>The checks expect the API described in docs/labs/m3-harden-an-endpoint.md:
 * {@code PUT /api/v1/profile} (the caller comes from the token) and
 * {@code POST /api/v1/profile/avatar}.
 */
@Tag("lab")
@Tag("m3")
@SpringBootTest
@AutoConfigureMockMvc
class M3ProfileLabCheck {
    static Path labRoot;

    @DynamicPropertySource
    static void dirs(DynamicPropertyRegistry registry) throws IOException {
        labRoot = Files.createTempDirectory("m3-labcheck");
        registry.add("app.web-root", () -> labRoot.resolve("www").toString());
        registry.add("app.upload-dir", () -> labRoot.resolve("uploads").toString());
    }

    @Autowired MockMvc mvc;
    @Autowired JdbcTemplate jdbc;

    private String displayNameOf(String userId) {
        return jdbc.queryForObject("SELECT display_name FROM profiles WHERE user_id = ?",
                String.class, userId);
    }

    private String bioOf(String userId) {
        return jdbc.queryForObject("SELECT bio FROM profiles WHERE user_id = ?", String.class, userId);
    }

    @Test
    @DisplayName("Task 1+2: a customer updates their own profile; values come back raw, not HTML-encoded")
    void customerUpdatesOwnProfile() throws Exception {
        MvcResult r = mvc.perform(put("/api/v1/profile")
                .with(LabUsers.customer(LabUsers.ALICE))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"displayName\":\"Anne-Marie O'Neil\",\"bio\":\"Likes <b>bold</b> & tea\"}"))
                .andReturn();
        assertThat(r.getResponse().getStatus()).as("PUT /api/v1/profile status").isEqualTo(200);
        String body = r.getResponse().getContentAsString();
        assertThat(body).as("response must hold the raw value; encoding happens where it is rendered")
                .contains("Likes <b>bold</b> & tea")
                .doesNotContain("&lt;").doesNotContain("&amp;");
        assertThat(displayNameOf(LabUsers.ALICE)).isEqualTo("Anne-Marie O'Neil");
    }

    @Test
    @DisplayName("Task 1: one customer cannot change another customer's profile (IDOR)")
    void customerCannotChangeSomeoneElsesProfile() throws Exception {
        mvc.perform(put("/api/v1/profile/" + LabUsers.BOB)
                .with(LabUsers.customer(LabUsers.ALICE))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"displayName\":\"Hacked by Alice\",\"bio\":\"oops\"}"));
        assertThat(displayNameOf(LabUsers.BOB))
                .as("Bob's profile was changed by Alice")
                .isEqualTo("Bob");
    }

    @Test
    @DisplayName("Task 1: SQL in the input is stored as text, never executed")
    void sqlInInputIsStoredLiterally() throws Exception {
        String payload = "x', display_name = 'pwned";
        mvc.perform(put("/api/v1/profile")
                .with(LabUsers.customer(LabUsers.ALICE))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"displayName\":\"Alice\",\"bio\":\"" + payload + "\"}"));
        assertThat(bioOf(LabUsers.ALICE)).as("bio must be stored exactly as sent").isEqualTo(payload);
        assertThat(displayNameOf(LabUsers.BOB)).isEqualTo("Bob");
    }

    @Test
    @DisplayName("Task 1: invalid input is rejected with 400")
    void invalidInputIsRejected() throws Exception {
        String tooLong = "A".repeat(200);
        int longStatus = mvc.perform(put("/api/v1/profile")
                .with(LabUsers.customer(LabUsers.ALICE))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"displayName\":\"" + tooLong + "\",\"bio\":\"ok\"}"))
                .andReturn().getResponse().getStatus();
        int scriptStatus = mvc.perform(put("/api/v1/profile")
                .with(LabUsers.customer(LabUsers.ALICE))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"displayName\":\"<script>alert(1)</script>\",\"bio\":\"ok\"}"))
                .andReturn().getResponse().getStatus();
        assertThat(longStatus).as("200-character display name").isEqualTo(400);
        assertThat(scriptStatus).as("display name containing markup").isEqualTo(400);
    }

    @Test
    @DisplayName("Task 1: only customers can update a profile")
    void otherRolesAreForbidden() throws Exception {
        int status = mvc.perform(put("/api/v1/profile")
                .with(LabUsers.support())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"displayName\":\"Support\",\"bio\":\"x\"}"))
                .andReturn().getResponse().getStatus();
        assertThat(status).as("a SUPPORT token on PUT /api/v1/profile").isEqualTo(403);
    }

    @Test
    @DisplayName("Task 1: CORS allows https://portal.example.ca only")
    void corsIsRestricted() throws Exception {
        for (String path : new String[] {"/api/v1/profile", "/api/v1/profile/" + LabUsers.ALICE}) {
            MvcResult evil = mvc.perform(options(path)
                    .header("Origin", "https://evil.example")
                    .header("Access-Control-Request-Method", "PUT")).andReturn();
            assertThat(evil.getResponse().getHeader("Access-Control-Allow-Origin"))
                    .as("CORS preflight from https://evil.example to " + path)
                    .isNull();
        }
        MvcResult portal = mvc.perform(options("/api/v1/profile")
                .header("Origin", "https://portal.example.ca")
                .header("Access-Control-Request-Method", "PUT")).andReturn();
        assertThat(portal.getResponse().getHeader("Access-Control-Allow-Origin"))
                .as("CORS preflight from the real portal")
                .isEqualTo("https://portal.example.ca");
    }

    @Test
    @DisplayName("Task 3: an HTML file disguised as a PNG is rejected (415)")
    void disguisedUploadIsRejected() throws Exception {
        MockMultipartFile fake = new MockMultipartFile("file", "avatar.png", "image/png",
                "<html><script>alert(document.domain)</script></html>".getBytes(StandardCharsets.UTF_8));
        int status = mvc.perform(multipart("/api/v1/profile/avatar").file(fake)
                .with(LabUsers.customer(LabUsers.ALICE))).andReturn().getResponse().getStatus();
        assertThat(status).as("upload of HTML content named avatar.png").isEqualTo(415);
    }

    @Test
    @DisplayName("Task 3: the client's file name is never used (no path traversal)")
    void clientFileNameIsIgnored() throws Exception {
        MockMultipartFile png = new MockMultipartFile("file", "../../escape.png", "image/png", tinyPng());
        MvcResult r = mvc.perform(multipart("/api/v1/profile/avatar").file(png)
                .with(LabUsers.customer(LabUsers.ALICE))).andReturn();
        assertThat(r.getResponse().getStatus()).as("upload of a real PNG").isEqualTo(201);
        assertThat(r.getResponse().getContentAsString())
                .as("the stored name must be generated by the server")
                .doesNotContain("escape").doesNotContain("..");
        try (Stream<Path> files = Files.walk(labRoot.getParent())) {
            assertThat(files.filter(p -> p.getFileName().toString().equals("escape.png")).toList())
                    .as("a file was written using the client's file name")
                    .isEmpty();
        }
        Path uploads = labRoot.resolve("uploads");
        assertThat(Files.isDirectory(uploads) && Files.list(uploads).findAny().isPresent())
                .as("the file must be stored in app.upload-dir, outside the web root")
                .isTrue();
    }

    private static byte[] tinyPng() throws IOException {
        BufferedImage img = new BufferedImage(2, 2, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(img, "png", out);
        return out.toByteArray();
    }
}
