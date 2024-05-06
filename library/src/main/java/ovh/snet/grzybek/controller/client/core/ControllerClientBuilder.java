package ovh.snet.grzybek.controller.client.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Builder class for creating a rest client for spring RestControllers.
 *
 * <p>This class builds {@code ControllerClient} instances that are proxies for the given
 * controller. You can customize the request and result actions:
 *
 * <ul>
 *   <li>Customize the request by calling {@link #customizeRequest(Consumer)}.
 *   <li>Customize the response by calling {@link #customizeResponse(Function)}.
 *   <li>Expect a specific status code by calling {@link #expectStatus(int)}.
 *   <li>Expect a specific status code and build the client by calling {@link
 *       #withExpectedStatus(int)}.
 *   <li>Build the client by calling {@link #build()}.
 * </ul>
 *
 * <p>You can create a {@code ControllerClient} instance by injecting {@link
 * ControllerClientFactory (Class)} bean and calling {@link
 * ControllerClientFactory#builder(Class)}.
 *
 * <p>Usage example:
 *
 * <pre>{@code
 * ControllerClient<MyController> client = controllerClientBuilderFactory.create(MyController.class)
 *     .customizeRequest(request -> request.header("Authorization", "Bearer token"))
 *     .expectStatus(HttpStatus.OK.value())
 *     .build();
 *
 * MyResponse response = client.someControllerMethod();
 * }</pre>
 */
public class ControllerClientBuilder<T> {

  private final Class<T> clazz;
  private final ObjectMapper objectMapper;
  private final MockMvc mockMvc;
  private final List<Consumer<RequestBuilder>> requestCustomizers = new ArrayList<>();
  private final List<Function<ResultActions, ResultActions>> responseCustomizers =
      new ArrayList<>();

  ControllerClientBuilder(Class<T> clazz, ObjectMapper objectMapper, MockMvc mockMvc) {
    this.clazz = clazz;
    this.objectMapper = objectMapper;
    this.mockMvc = mockMvc;
  }

  /**
   * Adds a customizer for the request. This allows for modification of the request before it is
   * sent, such as adding headers or request parameters.
   *
   * @param consumer a {@link Consumer} that customizes the {@link RequestBuilder}
   * @return the current instance of {@code ControllerClientBuilder} for fluent chaining
   */
  public ControllerClientBuilder<T> customizeRequest(Consumer<RequestBuilder> consumer) {
    requestCustomizers.add(consumer);
    return this;
  }

  /**
   * Adds a customizer for the response. This allows for modification or inspection of the response
   * after it is received, such as checking headers or content.
   *
   * @param consumer a {@link Function} that customizes the {@link ResultActions}
   * @return the current instance of {@code ControllerClientBuilder} for fluent chaining
   */
  public ControllerClientBuilder<T> customizeResponse(
      Function<ResultActions, ResultActions> consumer) {
    responseCustomizers.add(consumer);
    return this;
  }

  /**
   * Sets an expectation for the HTTP status code of the response. This is a convenience method for
   * asserting the status code in the response customizer.
   *
   * @param expectedStatus the expected HTTP status code
   * @return the current instance of {@code ControllerClientBuilder} for fluent chaining
   */
  public ControllerClientBuilder<T> expectStatus(int expectedStatus) {
    return customizeResponse(
        resultActions -> {
          try {
            return resultActions.andExpect(status().is(expectedStatus));
          } catch (Exception e) {
            throw new RuntimeException(e);
          }
        });
  }

  /**
   * Builds and returns a {@code ControllerClient} instance based on the current configuration of
   * this builder. The instance acts as a proxy for the specified controller, allowing invocation of
   * its methods as if making HTTP requests.
   *
   * @return a proxy instance of the specified controller class
   */
  public T build() {
    return new ControllerClient<T>(
            clazz,
            mockMvc,
            objectMapper,
            new ArrayList<>(requestCustomizers),
            new ArrayList<>(responseCustomizers))
        .getClient();
  }

  /**
   * Convenience method that sets an expected HTTP status code and builds the {@code
   * ControllerClient} instance. This is equivalent to calling {@link #expectStatus(int)} followed
   * by {@link #build()}.
   *
   * @param expectedStatus the expected HTTP status code
   * @return a proxy instance of the specified controller class with the expected status configured
   */
  public T withExpectedStatus(int expectedStatus) {
    return expectStatus(expectedStatus).build();
  }
}
