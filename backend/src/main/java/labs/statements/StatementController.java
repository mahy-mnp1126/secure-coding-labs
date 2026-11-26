package labs.statements;

import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Lets customers download their monthly statement as a PDF.
 */
@RestController
class StatementController {
    private static final Logger log = LoggerFactory.getLogger(StatementController.class);

    private final String statementsDir;

    StatementController(@Value("${app.statements-dir}") String statementsDir) {
        this.statementsDir = statementsDir;
    }

    @GetMapping("/api/v1/customers/{customerId}/statements")
    public ResponseEntity<byte[]> download(@PathVariable String customerId,
                                           @RequestParam String month) {
        try {
            Path file = Path.of(statementsDir).resolve(customerId).resolve(month + ".pdf");
            byte[] pdf = Files.readAllBytes(file);
            log.info("Statement downloaded: customer={} month={} file={}", customerId, month, file);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
                    .body(pdf);
        } catch (Exception e) {
            log.warn("Could not load statement", e);
            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_PLAIN)
                    .body(("Statement not available: " + e.getMessage()).getBytes());
        }
    }
}
