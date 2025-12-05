package ovh.snet.grzybek.controller.client.example;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import ovh.snet.grzybek.controller.client.core.ControllerClientFactory;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * This test class demonstrates how to use the Controller Client Factory to programmatically
 * create a builder for a controller client when more granular configuration of requests and
 * responses is required.
 * <p>
 * In scenarios where additional customization is necessary beyond the default behavior provided
 * by `@AutowireControllerClient`, the `ControllerClientFactory` allows for tailored request
 * building and response validation.
 * This enables:
 * <p>
 * - Adding custom headers, parameters, or other modifications to each request dynamically.
 * - Setting up expected HTTP status codes for responses to enforce validation on response statuses.
 * <p>
 * Key Concepts:
 * - The `ControllerClientFactory.builder(Class<T> controllerClass)` method initializes a
 * builder for the specified controller, allowing chainable configuration options.
 * - The `customizeRequest` method within the builder provides direct access to modify
 * the MockMvc request setup, such as setting custom headers.
 * - The `expectStatus` method allows specifying the expected HTTP status code for responses,
 * automatically validating that the response meets this expectation.
 * <p>
 * Usage:
 * - Use `ControllerClientFactory` for programmatically creating controller clients in tests
 * where custom behavior is required, such as adding headers or setting expected status codes.
 * <p>
 * Required Annotations:
 * - `@SpringBootTest` loads the full application context for integration testing.
 * - `@AutoConfigureMockMvc` configures MockMvc for Spring Boot test scenarios.
 */
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
