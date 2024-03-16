package ovh.snet.grzybek.controller.client.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.test.web.servlet.MockMvc;

@Service
public class ControllerClientBuilderFactory {

    private final ObjectMapper objectMapper;

    @Autowired(required = false)
    private MockMvc mockMvc;

    public ControllerClientBuilderFactory(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public <T> ControllerClientBuilder<T> builder(Class<T> clazz) {
        if (mockMvc == null) {
            throw new IllegalStateException("MockMvc is not set");
        }
        return new ControllerClientBuilder<>(clazz, objectMapper, mockMvc);
    }

    public <T> ControllerClientCaller<T> caller(Class<T> clazz) {
        return new ControllerClientCaller<>(builder(clazz));
    }
}
