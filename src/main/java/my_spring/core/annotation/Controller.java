package my_spring.core.annotation;

import my_spring.core.container.BeanScope;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Component
public @interface Controller {
    String name() default "";

    String initMethod() default "";

    String destroyMethod() default "";

    BeanScope scope() default BeanScope.SINGLETON;

    boolean lazyInit() default false;
}
