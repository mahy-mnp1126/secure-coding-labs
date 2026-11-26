package labs.m3;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import org.owasp.encoder.Encode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

// ============================================================================
// LAB SEED: DELIBERATELY VULNERABLE (Module 3, exercise 3.7)
// Flaws: IDOR (userId from the path), no validation, SQL injection,
// HTML-encoding inside JSON, CORS "*", and an upload that trusts the client's
// file name and writes into the (simulated) public web root.
// Do not copy this code into real projects.
// ============================================================================
@RestController
@CrossOrigin(origins = "*")
class ProfileController {
    private final JdbcTemplate jdbc;
    private final String webRoot;

    ProfileController(JdbcTemplate jdbc, @Value("${app.web-root}") String webRoot)
            throws IOException {
        this.jdbc = jdbc;
        this.webRoot = webRoot;
        Files.createDirectories(Path.of(webRoot, "avatars"));
    }

    @PutMapping("/api/v1/profile/{userId}")
    public Map<String, String> update(@PathVariable String userId,
                                      @RequestBody Map<String, String> body) {
        jdbc.update("UPDATE profiles SET display_name = '" + body.get("displayName")
                + "', bio = '" + body.get("bio") + "' WHERE user_id = '" + userId + "'");
        return Map.of("displayName", Encode.forHtml(body.get("displayName")),
                      "bio", Encode.forHtml(body.get("bio")));
    }

    @PostMapping("/api/v1/profile/{userId}/avatar")
    public String avatar(@PathVariable String userId,
                         @RequestParam("file") MultipartFile file) throws IOException {
        Path target = Path.of(webRoot, "avatars", file.getOriginalFilename());
        file.transferTo(target);
        return "/avatars/" + file.getOriginalFilename();
    }
}
