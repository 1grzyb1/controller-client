package ovh.snet.grzybek.controller.client.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Factory for creating {@link ControllerClientBuilder} and {@link ControllerClientCaller}
 * instances. It requires a {@link ObjectMapper} bean to be present.
 */
@Service
public class ControllerClientBuilderFactory {

  private final ObjectMapper objectMapper;

  @Autowired(required = false)
  private MockMvc mockMvc;

  public ControllerClientBuilderFactory(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  /** Creates a new instance of {@link ControllerClientBuilder} for the given controller class. */
  public <T> ControllerClientBuilder<T> builder(Class<T> clazz) {
    if (mockMvc == null) {
      throw new IllegalStateException("MockMvc is not set");
    }
    return new ControllerClientBuilder<>(clazz, objectMapper, mockMvc);
  }

  /** Creates a new instance of {@link ControllerClientCaller} for the given controller class. */
  public <T> ControllerClientCaller<T> caller(Class<T> clazz) {
    return new ControllerClientCaller<>(builder(clazz));
  }
}
