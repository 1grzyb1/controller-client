package ovh.snet.grzybek.controller.client.example;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import ovh.snet.grzybek.controller.client.core.ControllerClientCaller;
import ovh.snet.grzybek.controller.client.core.ControllerClientFactory;
import ovh.snet.grzybek.controller.client.core.annotation.AutowireControllerClientCaller;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * This test class demonstrates the use of `ControllerClientCaller` for testing Spring Boot
 * REST controllers, providing a structured approach to configure and verify request handling
 * behavior in test cases.
 * <p>
 * `ControllerClientCaller` is particularly useful when you need to dynamically configure
 * and invoke controller methods with specific request and response expectations. This includes:
 * <p>
 * - Specifying expected HTTP status codes for responses, enabling response validation.
 * - Defining additional request parameters, such as CSRF tokens, that are applied
 * programmatically in the test setup.
 * <p>
 * Key Concepts:
 * - `@AutowireControllerClientCaller` injects a `ControllerClientCaller` instance, allowing
 * for expressive testing by directly calling controller methods with defined expectations
 * for HTTP responses.
 * - `when` method: Specifies the controller method to be called.
 * - `thenStatus` method: Sets the expected HTTP status code, ensuring the response meets the
 * specified status.
 * - `execute` method: Executes the configured request and returns the response, which can be
 * asserted based on the expected outcome.
 * <p>
 * Usage Scenarios:
 * - Use `ControllerClientCaller` when you need to verify specific response statuses, headers,
 * or other request attributes dynamically. It is especially helpful for more complex
 * test setups where multiple attributes need to be verified, such as status codes or CSRF
 * tokens.
 * <p>
 * Required Annotations:
 * - `@SpringBootTest` loads the full application context for integration testing.
 * - `@AutoConfigureMockMvc` configures MockMvc for Spring Boot test scenarios.
 * - `@AutowireControllerClientCaller` automatically wires the `ControllerClientCaller` instance.
 */
@SpringBootTest
@AutoConfigureMockMvc
class CallerConfigurationExamples {

  @Autowired private ControllerClientFactory controllerClientFactory;
  @AutowireControllerClientCaller
  private ControllerClientCaller<ExampleController> clientCaller;

  @Test
  void checkExpectedStatusUsingCaller() {
    var result =
        (ExampleResponse)
            clientCaller
                .when(ExampleController::exampleMethod)
                .thenStatus(HttpStatus.OK.value())
                .execute();

    assertThat(result.message()).isEqualTo("Hello world!");
  }

  @Test
  void checkInvalidStatusWhenEnumInResponse() {
    var result =
            (ExampleResponse)
                    clientCaller
                            .when(ExampleController::enumExample)
                            .thenStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .execute();
  }

  @Test
  void addCsrfToken() {
    var builder =
            controllerClientFactory
                    .builder(ExampleController.class)
                    .expectStatus(HttpStatus.OK.value());
    var clientCaller = controllerClientFactory.caller(builder);
    var result =
            (ExampleResponse)
                    clientCaller
                            .when(ExampleController::exampleMethod)
                            .thenStatus(HttpStatus.OK.value())
                            .execute();

    assertThat(result.message()).isEqualTo("Hello world!");
  }
}
