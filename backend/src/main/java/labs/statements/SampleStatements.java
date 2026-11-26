package labs.statements;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/** Writes two synthetic sample statements at startup so the endpoint has something to serve. */
@Component
class SampleStatements implements ApplicationRunner {
    private final Path dir;

    SampleStatements(@Value("${app.statements-dir}") String dir) {
        this.dir = Path.of(dir);
    }

    @Override
    public void run(ApplicationArguments args) throws IOException {
        write("a11ce000-0000-4000-8000-000000000001", "2026-10", "Alice - October 2026 statement");
        write("b0b00000-0000-4000-8000-000000000002", "2026-10", "Bob - October 2026 statement");
    }

    private void write(String customerId, String month, String text) throws IOException {
        Path customerDir = Files.createDirectories(dir.resolve(customerId));
        String pdf = "%PDF-1.4\n% synthetic lab statement: " + text + "\n%%EOF\n";
        Files.writeString(customerDir.resolve(month + ".pdf"), pdf, StandardCharsets.US_ASCII);
    }
}
