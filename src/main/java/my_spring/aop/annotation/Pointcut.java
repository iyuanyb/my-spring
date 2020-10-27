package my_spring.aop.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 切点，被 {@code @Aspect} 表示的类必须包含一个被 {@code @Pointcut} 注解标记的方法。
 *
 * 仅支持的三种格式：
 *     {@code "pkg1.pkg2.*"} 表示包 {@code "pkg1.pkg2"} 及其子包下的所有类的所有
 *     {@code "pkg1.pkg2.ClassA.*"} 表示类 {@code "pkg1.pkg2.ClassA.*"} 的所有
 *     {@code "pkg1.pkg2.ClassA.method"} 指定某个方法，暂不支持重载
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Pointcut {
    String value();
}
