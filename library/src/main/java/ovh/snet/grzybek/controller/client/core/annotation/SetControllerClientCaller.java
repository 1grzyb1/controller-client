package ovh.snet.grzybek.controller.client.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SetControllerClientCaller {

    Class<? extends ControllerClientAnnotationCustomizer> customizer() default DefaultControllerClientAnnotationCustomizer.class;
}
