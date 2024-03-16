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

    public ControllerClientCaller(ControllerClientBuilder<T> builder) {
        this.builder = builder;
    }

    public ControllerClientCaller<T> when(Function<T, Object> controller) {
        this.controllerCall = controller;
        assertOneConsumer();
        return this;
    }

    public ControllerClientCaller<T> whenConsume(Consumer<T> controller) {
        this.controllerConsumer = controller;
        assertOneConsumer();
        return this;
    }

    public ControllerClientCaller<T> then(Function<ResultActions, ResultActions> consumer) {
        builder.customizeResponse(consumer);
        return this;
    }

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

    public <R> R execute(Class<R> responseClazz) {
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

