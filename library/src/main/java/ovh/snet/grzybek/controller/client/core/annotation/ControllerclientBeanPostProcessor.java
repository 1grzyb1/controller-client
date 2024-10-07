package ovh.snet.grzybek.controller.client.core.annotation;

import jakarta.annotation.Nullable;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import ovh.snet.grzybek.controller.client.core.ControllerClientBuilder;
import ovh.snet.grzybek.controller.client.core.ControllerClientFactory;

import java.lang.reflect.Field;
import java.util.Optional;

@Component
class ControllerclientBeanPostProcessor implements BeanPostProcessor {

    private final ControllerClientFactory controllerClientFactory;
    private final Optional<ControllerClientAnnotationCustomizer> customizer;

    ControllerclientBeanPostProcessor(ControllerClientFactory controllerClientFactory, @Nullable ControllerClientAnnotationCustomizer customizer) {
        this.controllerClientFactory = controllerClientFactory;
        this.customizer = Optional.ofNullable(customizer);
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Field[] fields = bean.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(ControllerClient.class)) {
                try {
                    field.setAccessible(true);
                    Class<?> fieldType = field.getType();
                    var builder = (ControllerClientBuilder<Object>) controllerClientFactory.builder(fieldType);
                    var client = getClient(builder);
                    field.set(bean, client);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Failed to inject client for field: " + field.getName(), e);
                }
            }
        }
        return bean;
    }

    private Object getClient(ControllerClientBuilder<Object> builder) {
        if (customizer.isPresent()) {
            return customizer.get().customize(builder).build();
        } else {
            return builder.build();
        }
    }
}
