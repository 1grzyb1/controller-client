package ovh.snet.grzybek.controller.client.example;

import org.springframework.stereotype.Component;
import ovh.snet.grzybek.controller.client.core.ControllerClientBuilder;
import ovh.snet.grzybek.controller.client.core.annotation.ControllerClientAnnotationCustomizer;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Component
class ControllerClientCustomizer implements ControllerClientAnnotationCustomizer {

    @Override
    public ControllerClientBuilder<Object> customize(ControllerClientBuilder<Object> builder) {
        return builder.customizeResponse(
                resultActions -> {
                    try {
                        return resultActions.andExpect(status().is2xxSuccessful());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
        );
    }
}
