package my_spring.aop.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 环绕通知，被注解的方法可以包含 JoinPoint 参数；也可传入实际被代理方法的参数，通过参数名匹配。
 *
 * Example:
 * {@code
 *     @Around
 *     public Object around(JoinPoint jp, arg1, ...) {
 *         try {
 *             ...
 *             Object ret = jp.proceed()
 *             ...
 *         } catch(..) {
 *             ...
 *         } finally {
 *             ...
 *         }
 *     }
 * }
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Around {
}
