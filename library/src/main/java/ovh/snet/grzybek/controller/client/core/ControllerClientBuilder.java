package ovh.snet.grzybek.controller.client.core;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;

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
 * ControllerClientBuilderFactory(Class)} bean and calling {@link
 * ControllerClientBuilderFactory#builder(Class)}.
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

    public ControllerClientBuilder<T> customizeRequest(Consumer<RequestBuilder> consumer) {
        requestCustomizers.add(consumer);
        return this;
    }

    public ControllerClientBuilder<T> customizeResponse(
            Function<ResultActions, ResultActions> consumer) {
        responseCustomizers.add(consumer);
        return this;
    }

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

    public T build() {
        return new ControllerClient<T>(
                clazz,
                mockMvc,
                objectMapper,
                new ArrayList<>(requestCustomizers),
                new ArrayList<>(responseCustomizers))
                .getClient();
    }

    public T withExpectedStatus(int expectedStatus) {
        return expectStatus(expectedStatus).build();
    }
}
