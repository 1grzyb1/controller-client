package ovh.snet.grzybek.controller.client.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import ovh.snet.grzybek.controller.client.core.ControllerClientFactory;
import ovh.snet.grzybek.controller.client.core.ControllerResponse;
import ovh.snet.grzybek.controller.client.core.RespondingControllerClient;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
class RespondingControllerClientExample {

    @Autowired
    private ControllerClientFactory controllerClientFactory;

    private RespondingControllerClient<ExampleController> exampleController;

    @BeforeEach
    void setUp() {
        exampleController = controllerClientFactory.respondingClient(ExampleController.class);
    }

    @Test
    void shouldReturnInternalServerError() {
        var response = exampleController.executeFunction(ExampleController::enumExample);
        assertThat(response.statusCode()).isEqualTo(500);
    }

    @Test
    void basicGet() {
        ControllerResponse<ExampleResponse> response = exampleController.executeFunction(ExampleController::exampleMethod);
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.response().message()).isEqualTo("Hello world!");
    }

    @Test
    void postWithBody() {
        var request = new ExampleRequest("Test message");
        ControllerResponse<ExampleResponse> response = exampleController.executeFunction(c -> c.bodyExample(request));
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.response().message()).isEqualTo("Received: Test message");
    }
}
