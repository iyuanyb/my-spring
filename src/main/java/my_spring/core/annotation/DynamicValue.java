package my_spring.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 可以在不重启应用的情况下实时异步更新字段的值，被注解的字段必须使用 volatile 修饰。
 * @see Value
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DynamicValue {
    String value();
}
