package ovh.snet.grzybek.controller.client.core.annotation;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import ovh.snet.grzybek.controller.client.core.ControllerClientBuilder;
import ovh.snet.grzybek.controller.client.core.ControllerClientFactory;
import ovh.snet.grzybek.controller.client.core.ControllerClientCaller;
import ovh.snet.grzybek.controller.client.core.RespondingControllerClient;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Optional;
import java.util.function.BiFunction;

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
            try {
                if (field.isAnnotationPresent(AutowireControllerClient.class)) {
                    setField(bean, field, AutowireControllerClient.class, null,
                            (factory, builder) -> builder.build());
                } else if (field.isAnnotationPresent(AutowireControllerClientCaller.class)) {
                    setField(bean, field, AutowireControllerClientCaller.class, ControllerClientCaller.class,
                            ControllerClientFactory::caller);
                } else if (field.isAnnotationPresent(AutowireRespondingControllerClient.class)) {
                    setField(bean, field, AutowireRespondingControllerClient.class, RespondingControllerClient.class,
                            ControllerClientFactory::respondingClient);
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Failed to inject client for field: " + field.getName(), e);
            }
        }
        return bean;
    }

    private void setField(Object bean, Field field, Class<? extends Annotation> annotationClass, Type classType,
                          BiFunction<ControllerClientFactory, ControllerClientBuilder<Object>, Object> clientFactoryFunction)
            throws IllegalAccessException {
        field.setAccessible(true);
        var fieldType = getFieldType(field, classType);
        final var builder = (ControllerClientBuilder<Object>) controllerClientFactory.builder(fieldType);
        var annotation = field.getAnnotation(annotationClass);
        var customizer = instantiateCustomizer(getCustomizerClass(annotation));
        customizer.ifPresent(c -> c.customize(builder));
        var client = clientFactoryFunction.apply(controllerClientFactory, builder);
        field.set(bean, client);
    }

    private static Class<?> getFieldType(Field field, Type classType) {
        if (classType != null) {
            return getClassType(field, classType);
        }
        return field.getType();
    }

    private static Class<?> getClassType(Field field, Type classType) {
        Type genericFieldType = field.getGenericType();
        ParameterizedType parameterizedType = (ParameterizedType) genericFieldType;
        Type rawType = parameterizedType.getRawType();
        assert rawType == classType;
        Type[] typeArguments = parameterizedType.getActualTypeArguments();
        return (Class<?>) typeArguments[0];
    }

    private Class<? extends ControllerClientAnnotationCustomizer> getCustomizerClass(Annotation annotation) {
        try {
            Method method = annotation.annotationType().getMethod("customizer");
            return (Class<? extends ControllerClientAnnotationCustomizer>) method.invoke(annotation);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get customizer class from annotation: "
                    + annotation.annotationType().getName(), e);
        }
    }

    private Optional<ControllerClientAnnotationCustomizer> instantiateCustomizer(
            Class<? extends ControllerClientAnnotationCustomizer> customizerClass) {
        try {
            if (customizerClass == DefaultControllerClientAnnotationCustomizer.class) {
                return Optional.empty();
            }
            return Optional.of(customizerClass.getDeclaredConstructor().newInstance());
        } catch (Exception e) {
            throw new RuntimeException("Failed to instantiate customizer: " + customizerClass.getName(), e);
        }
    }
}
