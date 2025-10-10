package com.jkmconfecciones.Integrador_app;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.mail.host=smtp.gmail.com",
    "spring.mail.port=587",
    "spring.mail.username=test@example.com",
    "spring.mail.password=test",
    "spring.mail.properties.mail.smtp.auth=true",
    "spring.mail.properties.mail.smtp.starttls.enable=true"
})
class IntegradorAppApplicationTests {

    @Test
    void contextLoads() {
        // This test only verifies if the Spring context can be loaded
    }

}
