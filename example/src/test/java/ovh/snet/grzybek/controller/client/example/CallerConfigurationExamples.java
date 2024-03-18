package ovh.snet.grzybek.controller.client.example;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import ovh.snet.grzybek.controller.client.core.ControllerClientBuilderFactory;

@SpringBootTest
@Import(ControllerClientBuilderFactory.class)
@AutoConfigureMockMvc
class CallerConfigurationExamples {

  @Autowired private ControllerClientBuilderFactory controllerClientBuilderFactory;

  @Test
  void checkExpectedStatusUsingCaller() {
    var clientCaller = controllerClientBuilderFactory.caller(ExampleController.class);
    var result =
        clientCaller
            .when(ExampleController::exampleMethod)
            .thenStatus(HttpStatus.OK.value())
            .execute(ExampleResponse.class);

    assertThat(result.message()).isEqualTo("Hello world!");
  }
}
