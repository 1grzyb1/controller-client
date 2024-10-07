package ovh.snet.grzybek.controller.client.core.annotation;

import ovh.snet.grzybek.controller.client.core.ControllerClientBuilder;

public interface ControllerClientAnnotationCustomizer {

    ControllerClientBuilder<Object> customize(ControllerClientBuilder<Object> builder);
}
