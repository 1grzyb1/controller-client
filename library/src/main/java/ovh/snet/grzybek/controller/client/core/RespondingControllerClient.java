package ovh.snet.grzybek.controller.client.core;

import org.springframework.mock.web.MockHttpServletResponse;

import java.util.function.Consumer;
import java.util.function.Function;

public class RespondingControllerClient<T> {

    private final ControllerClientBuilder<T> builder;
    private MockHttpServletResponse lastResponse;

    /**
     * Constructs a {@code RespondingControllerClient} with the specified {@link ControllerClientBuilder}.
     * The builder is used to configure the client and handle the HTTP response.
     *
     * @param builder the builder used to construct and configure the controller client
     */
    RespondingControllerClient(ControllerClientBuilder<T> builder) {
        this.builder = builder;
        builder.handleResponse(r -> lastResponse = r);
    }

    /**
     * Executes a controller action that returns a result. The controller action is represented by a
     * {@link Function} which takes the controller instance as input and returns the result.
     * The HTTP response is captured and wrapped in a {@link ControllerResponse} object alongside
     * the result of the controller action.
     *
     * @param controller a {@link Function} representing the controller action to invoke, which returns a result
     * @param <R>        the type of the result returned by the controller action
     * @return a {@link ControllerResponse} containing the captured HTTP response and the result of the controller action
     * @throws IllegalStateException if a controller action has already been defined
     */
    public <R> ControllerResponse<R> executeFunction(Function<T, Object> controller) {
        var response = (R) controller.apply(builder.build());
        return new ControllerResponse<>(lastResponse, response);
    }

    /**
     * Executes a controller action that does not return a result (void). The controller action is represented by a
     * {@link Consumer} which takes the controller instance as input. The HTTP response is captured and wrapped
     * in a {@link ControllerResponse} object.
     *
     * @param controller a {@link Consumer} representing the controller action to invoke, which does not return a result
     * @return a {@link ControllerResponse} containing the captured HTTP response
     * @throws IllegalStateException if a controller action has already been defined
     */
    public ControllerResponse<Void> executeConsumer(Consumer<T> controller) {
        controller.accept(builder.build());
        return new ControllerResponse<>(lastResponse);
    }
}
