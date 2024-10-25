package ovh.snet.grzybek.controller.client.core.annotation;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import ovh.snet.grzybek.controller.client.core.ControllerClientBuilder;
import ovh.snet.grzybek.controller.client.core.ControllerClientCaller;
import ovh.snet.grzybek.controller.client.core.ControllerClientFactory;
import ovh.snet.grzybek.controller.client.core.RespondingControllerClient;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

@Component
class ControllerclientBeanPostProcessor implements BeanPostProcessor {

    private final ControllerClientFactory controllerClientFactory;

    ControllerclientBeanPostProcessor(ControllerClientFactory controllerClientFactory) {
        this.controllerClientFactory = controllerClientFactory;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Field[] fields = bean.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(AutowireControllerClient.class)) {
                setControllerClient(bean, field);
            }

            if (field.isAnnotationPresent(AutowireControllerClientCaller.class)) {
                setControllerClientCaller(bean, field);
            }

            if (field.isAnnotationPresent(AutowireRespondingControllerClient.class)) {
                setRespondingControllerClient(bean, field);
            }
        }
        return bean;
    }

    private void setControllerClient(Object bean, Field field) {
        try {
            field.setAccessible(true);
            Class<?> fieldType = field.getType();
            var builder = (ControllerClientBuilder<Object>) controllerClientFactory.builder(fieldType);

            var annotation = field.getAnnotation(AutowireControllerClient.class);
            ControllerClientAnnotationCustomizer customizer = instantiateCustomizer(annotation.customizer());

            var client = getClient(builder, customizer);
            field.set(bean, client);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to inject client for field: " + field.getName(), e);
        }
    }

    private void setControllerClientCaller(Object bean, Field field) {
        try {
            field.setAccessible(true);
            Class<?> subtypeClass = getClassType(field, ControllerClientCaller.class);
            var builder = (ControllerClientBuilder<Object>) controllerClientFactory.builder(subtypeClass);

            var annotation = field.getAnnotation(AutowireControllerClientCaller.class);
            ControllerClientAnnotationCustomizer customizer = instantiateCustomizer(annotation.customizer());

            var caller = getClientCaller(builder, customizer);
            field.set(bean, caller);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to inject client for field: " + field.getName(), e);
        }
    }

    private void setRespondingControllerClient(Object bean, Field field) {
        try {
            field.setAccessible(true);
            Class<?> subtypeClass = getClassType(field, RespondingControllerClient.class);
            var builder = (ControllerClientBuilder<Object>) controllerClientFactory.builder(subtypeClass);

            var annotation = field.getAnnotation(AutowireRespondingControllerClient.class);
            ControllerClientAnnotationCustomizer customizer = instantiateCustomizer(annotation.customizer());

            var caller = getRespondingClient(builder, customizer);
            field.set(bean, caller);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to inject client for field: " + field.getName(), e);
        }
    }

    private static Class<?> getClassType(Field field, Type classType) {
        Type genericFieldType = field.getGenericType();
        ParameterizedType parameterizedType = (ParameterizedType) genericFieldType;
        Type rawType = parameterizedType.getRawType();
        assert rawType == classType;
        Type[] typeArguments = parameterizedType.getActualTypeArguments();
        Class<?> subtypeClass = (Class<?>) typeArguments[0];
        return subtypeClass;
    }

    private Object getClient(ControllerClientBuilder<Object> builder, ControllerClientAnnotationCustomizer customizer) {
        if (customizer != null) {
            return customizer.customize(builder).build();
        } else {
            return builder.build();
        }
    }

    private Object getClientCaller(ControllerClientBuilder<Object> builder, ControllerClientAnnotationCustomizer customizer) {
        if (customizer != null) {
            return controllerClientFactory.caller(customizer.customize(builder));
        } else {
            return controllerClientFactory.caller(builder);
        }
    }

    private Object getRespondingClient(ControllerClientBuilder<Object> builder, ControllerClientAnnotationCustomizer customizer) {
        if (customizer != null) {
            return controllerClientFactory.respondingClient(customizer.customize(builder));
        } else {
            return controllerClientFactory.respondingClient(builder);
        }
    }


    private ControllerClientAnnotationCustomizer instantiateCustomizer(Class<? extends ControllerClientAnnotationCustomizer> customizerClass) {
        try {
            if (customizerClass == DefaultControllerClientAnnotationCustomizer.class) {
                return null;
            }
            return customizerClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Failed to instantiate customizer: " + customizerClass.getName(), e);
        }
    }
}

