package ovh.snet.grzybek.controller.client.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Factory for creating {@link ControllerClientBuilder} and {@link ControllerClientCaller}
 * instances. It requires a {@link ObjectMapper} bean to be present.
 */
@Service
public class ControllerClientFactory {

    private final ObjectMapper objectMapper;

    @Autowired(required = false)
    private MockMvc mockMvc;

    public ControllerClientFactory(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Creates a new instance of {@link ControllerClient} for the given controller class that expects 2xx status.
     */
    public <T> T create(Class<T> clazz) {
        return builder(clazz).customizeResponse(
                resultActions -> {
                    try {
                        return resultActions.andExpect(status().is2xxSuccessful());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
        ).build();
    }

    /**
     * Creates a new instance of {@link ControllerClientBuilder} for the given controller class.
     */
    public <T> ControllerClientBuilder<T> builder(Class<T> clazz) {
        if (mockMvc == null) {
            throw new IllegalStateException("MockMvc is not set");
        }
        return new ControllerClientBuilder<>(clazz, objectMapper, mockMvc);
    }

    /**
     * Creates a new instance of {@link ControllerClientCaller} for the given controller class.
     */
    public <T> ControllerClientCaller<T> caller(Class<T> clazz) {
        return new ControllerClientCaller<>(builder(clazz));
    }
}
