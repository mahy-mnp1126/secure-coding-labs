package labs.statements;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import labs.LabUsers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class StatementControllerTest {
    @Autowired MockMvc mvc;

    @Test
    void customerDownloadsStatement() throws Exception {
        mvc.perform(get("/api/v1/customers/" + LabUsers.ALICE + "/statements")
                .param("month", "2026-10")
                .with(LabUsers.customer(LabUsers.ALICE)))
           .andExpect(status().isOk())
           .andExpect(content().contentType(MediaType.APPLICATION_PDF));
    }
}
