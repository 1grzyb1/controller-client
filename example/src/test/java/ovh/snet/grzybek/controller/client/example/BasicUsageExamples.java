package ovh.snet.grzybek.controller.client.example;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import ovh.snet.grzybek.controller.client.core.annotation.AutowireControllerClient;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * This test class demonstrates the basic usage of the Controller Client library
 * for testing Spring Boot REST controllers.
 * The library allows direct calls to
 * controller methods using proxy instances, abstracting away MockMvc setup and
 * response deserialization, making tests concise and type-safe.
 * <p>
 * The Controller Client library leverages the Spring MockMvc framework under the hood,
 * simplifying typical REST API testing by handling setup and validation in a more
 * readable, intuitive way.
 * <p>
 * In this example:
 * - The `@AutowireControllerClient` annotation is used to inject a proxy instance of
 * the `ExampleController`, enabling direct method calls on the controller.
 * - Each test demonstrates how to interact with different types of controller endpoints,
 * including GET and POST requests, requests with path variables and parameters, and file uploads.
 * - The Controller Client also supports assertions for responses and status codes, which can
 * be customized via `ControllerClientCustomizer`.
 * <p>
 * Setup:
 * - The `ControllerClientCustomizer` class is configured to automatically verify the
 * response status is 2xx for each test.
 * This can be customized as needed.
 * <p>
 * Required Annotations:
 * - `@SpringBootTest` loads the full application context for integration testing.
 * - `@AutoConfigureMockMvc` configures MockMvc for use within Spring Boot tests.
 * - `@AutowireControllerClient` injects the controller client proxy with optional
 * customizations.
 */
@SpringBootTest
@AutoConfigureMockMvc
class BasicUsageExamples {
    MockMvc mockMvc;
    @AutowireControllerClient(customizer = ControllerClientCustomizer.class)
    private ExampleController exampleController;

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
    void getWithNullParam() {
        var response = exampleController.paramExample(null);
        assertThat(response.message()).isEqualTo("Received: null");
    }

    @Test
    void getWithPath() {
        var response = exampleController.pathExample("Test path");
        assertThat(response.message()).isEqualTo("Received: Test path");
    }

    @Test
    void getWithNullPath() {
        assertThatThrownBy(() -> exampleController.pathExample(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Path variable")
                .hasMessageContaining("pathExample");
    }


    @Test
    void getList() {
        var response = exampleController.listExample();
        assertThat(response).hasSize(2);
        assertThat(response.get(0).message()).isEqualTo("a");
        assertThat(response.get(1).message()).isEqualTo("b");
    }

    @Test
    void getParametrizedType() {
        var response = exampleController.twoType();
        assertThat(response.getFirst()).isEqualTo("a");
        assertThat(response.getSecond()).isEqualTo(1);
    }

    @Test
    void uploadFile() {
        var file = new MockMultipartFile("file", "test.txt", "text/plain", "Hello, World!".getBytes());
        var response = exampleController.uploadFile(file);
        assertThat(response.message()).isEqualTo("Uploaded File: test.txt (text/plain)");
    }

    @Test
    void getWithListInRequestParam() {
        var response = exampleController.getListParam(List.of("a", "b"));
        assertThat(response).isEqualTo("a,b");
    }
}
