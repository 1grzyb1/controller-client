package ovh.snet.grzybek.controller.client.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import ovh.snet.grzybek.controller.client.core.ControllerClientBuilderFactory;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import(ControllerClientBuilderFactory.class)
@AutoConfigureMockMvc
class BasicUsageExamples {

  @Autowired private ControllerClientBuilderFactory controllerClientBuilderFactory;

  private ExampleController exampleController;

  @BeforeEach
  void setUp() {
    exampleController = controllerClientBuilderFactory.builder(ExampleController.class).build();
  }

  @Test
  void basicGet() {
    var response = exampleController.exampleMethod();
    assertThat(response.message()).isEqualTo("Hello world!");
  }

  @Test
  void postWithBody() {
    var request = new ExampleRequest("Test message");
    var response = exampleController.bodyExample(request);
    assertThat(response.message()).isEqualTo("Received: Test message");
  }

  @Test
  void getWithParam() {
    var response = exampleController.paramExample("param");
    assertThat(response.message()).isEqualTo("Received: param");
  }

  @Test
  void getWithPath() {
    var response = exampleController.pathExample("Test path");
    assertThat(response.message()).isEqualTo("Received: Test path");
  }

  @Test
  void customizeRequest() {
    var client =
        controllerClientBuilderFactory
            .builder(ExampleController.class)
            .customizeRequest(
                request ->
                    ((MockHttpServletRequestBuilder) request).header("Authorization", "token"))
            .build();
    var response = client.exampleMethod();
    assertThat(response.message()).isEqualTo("Hello world!");
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
