package ovh.snet.grzybek.controller.client.core;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.function.Consumer;
import java.util.function.Function;
import org.springframework.test.web.servlet.ResultActions;

/**
 * Caller for resat controller clients that lets write tests in more described way.
 *
 * <p>This class calls {@code ControllerClient} instances method and add asserts after the call.
 * After calling execute() method response Object is returned.
 *
 * <p>Usage example:
 *
 * <pre>{@code
 * var changeRequestClientCaller =
 *         controllerClientBuilderFactory.caller(ChangeRequestAttachmentsRestController.class);
 * changeRequestClientCaller
 *         .whenConsume(client -> client.deleteAttachment(changeRequestId, "files/" + UUID.randomUUID()))
 *         .thenStatus(404)
 *         .execute();
 * }</pre>
 *
 * Important!! Each call to Controller needs to be performed on a different instance of the Caller
 * because expectations (.andExpect()) are not resetable.
 */
public class ControllerClientCaller<T> {

  private final ControllerClientBuilder<T> builder;
  private Function<T, Object> controllerCall;
  private Consumer<T> controllerConsumer;

  ControllerClientCaller(ControllerClientBuilder<T> builder) {
    this.builder = builder;
  }

  /**
   * Specifies the controller action to be tested using a {@link Function}. This action is expected
   * to return a result, which can be further inspected or ignored.
   *
   * @param controller a {@link Function} representing the controller action to invoke
   * @return this {@code ControllerClientCaller} instance for chaining further configurations
   * @throws IllegalStateException if a controller action has already been defined
   */
  public ControllerClientCaller<T> when(Function<T, Object> controller) {
    this.controllerCall = controller;
    assertOneConsumer();
    return this;
  }

  /**
   * Specifies the controller action to be tested using a {@link Consumer}. This variant is used for
   * controller actions that return void.
   *
   * @param controller a {@link Consumer} representing the controller action to invoke
   * @return this {@code ControllerClientCaller} instance for chaining further configurations
   * @throws IllegalStateException if a controller action has already been defined
   */
  public ControllerClientCaller<T> when(Consumer<T> controller) {
    this.controllerConsumer = controller;
    assertOneConsumer();
    return this;
  }

  /**
   * Adds a custom response assertion to be applied after the controller action is invoked. This
   * allows for detailed inspection of the response, such as checking headers, content, or status
   * codes.
   *
   * @param consumer a {@link Function} that defines the response assertion
   * @return this {@code ControllerClientCaller} instance for chaining further configurations
   */
  public ControllerClientCaller<T> then(Function<ResultActions, ResultActions> consumer) {
    builder.customizeResponse(consumer);
    return this;
  }

  /**
   * A convenience method for adding an assertion on the HTTP status code of the response.
   *
   * @param statusCode the expected HTTP status code
   * @return this {@code ControllerClientCaller} instance for chaining further configurations
   */
  public ControllerClientCaller<T> thenStatus(int statusCode) {
    then(
        result -> {
          try {
            return result.andExpect(status().is(statusCode));
          } catch (Exception e) {
            throw new RuntimeException(e);
          }
        });
    return this;
  }

  /**
   * Executes the configured controller action and returns the result. This method builds the
   * controller client, invokes the specified action, applies any response assertions, and returns
   * the action's result if applicable.
   *
   * @param <R> the type of the response
   * @return the result of the controller action, or {@code null} if the action returns void
   * @throws IllegalStateException if both a {@link Function} and a {@link Consumer} have been
   *     defined
   */
  public <R> R execute() {
    assertOneConsumer();
    if (controllerConsumer != null) {
      controllerConsumer.accept(builder.build());
      return null;
    }
    return (R) controllerCall.apply(builder.build());
  }

  private void assertOneConsumer() {
    if (controllerCall != null && controllerConsumer != null) {
      throw new IllegalStateException("Only one controller call can be defined");
    }
  }
}
