package ovh.snet.grzybek.controller.client.example;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import ovh.snet.grzybek.controller.client.core.annotation.AutowireControllerClient;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test to allow creating proxies for controllers whose constructors require non-null arguments.
 */
@SpringBootTest
@AutoConfigureMockMvc
class NotNullConstructorTest {

    @AutowireControllerClient(customizer = ControllerClientCustomizer.class)
    private NotNullConstructorRestController client;

    @Test
    void basicGet() {
        var response = client.exampleMethod();
        assertThat(response).isEqualTo("Hello world!");
    }
}
