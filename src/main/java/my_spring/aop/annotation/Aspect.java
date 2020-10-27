package my_spring.aop.annotation;

import my_spring.core.annotation.Component;

import java.lang.annotation.*;

/**
 * 标记注解，注解在一个类上，表示一个切面类。
 */
@Component
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Aspect {
    /**
     * 切面增强的顺序，order越小越先被采用。
     */
    int order() default Integer.MAX_VALUE;
}
