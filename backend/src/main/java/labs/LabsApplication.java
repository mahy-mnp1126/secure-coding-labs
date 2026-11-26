package labs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * secure-coding-labs: DELIBERATELY VULNERABLE TRAINING APPLICATION.
 *
 * <p>It binds to 127.0.0.1:8081 only. Never expose it to a network and never deploy it.
 * All data is synthetic.
 */
@SpringBootApplication
public class LabsApplication {

    public static void main(String[] args) {
        SpringApplication.run(LabsApplication.class, args);
    }
}
