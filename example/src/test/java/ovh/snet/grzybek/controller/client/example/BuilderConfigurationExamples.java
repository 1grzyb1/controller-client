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
class BuilderConfigurationExamples {

  @Autowired private ControllerClientBuilderFactory controllerClientBuilderFactory;

  @Test
  void customizeRequest() {
    var client =
        controllerClientBuilderFactory
            .builder(ExampleController.class)
            .customizeRequest(
                request ->
                    ((MockHttpServletRequestBuilder) request).header("X-Example-Header", "token"))
            .build();
    var response = client.headerExample(null);
    assertThat(response.message()).isEqualTo("Header value: token");
  }

  @Test
  void checkExpectedStatusUsingBuilder() {
    var client =
        controllerClientBuilderFactory
            .builder(ExampleController.class)
            .expectStatus(HttpStatus.OK.value())
            .build();
    var response = client.exampleMethod();
    assertThat(response.message()).isEqualTo("Hello world!");
  }
}
