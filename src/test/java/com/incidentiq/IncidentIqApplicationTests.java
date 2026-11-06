package com.incidentiq;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
        "DB_USERNAME=test-user",
        "DB_PASSWORD=test-password"
})
class IncidentIqApplicationTests {

    @Test
    void contextLoads() {
    }
}
