package ovh.snet.grzybek.controller.client.example;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import ovh.snet.grzybek.controller.client.core.ControllerClientFactory;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
class BuilderConfigurationExamples {

  @Autowired private ControllerClientFactory controllerClientFactory;

  @Test
  void customizeRequest() {
    var client =
        controllerClientFactory
            .builder(ExampleController.class)
            .customizeRequest(
                request -> request.header("X-Example-Header", "token"))
            .build();
    var response = client.headerExample(null);
    assertThat(response.message()).isEqualTo("Header value: token");
  }

  @Test
  void checkExpectedStatusUsingBuilder() {
    var client =
        controllerClientFactory
            .builder(ExampleController.class)
            .expectStatus(HttpStatus.OK.value())
            .build();
    var response = client.exampleMethod();
    assertThat(response.message()).isEqualTo("Hello world!");
  }
}
