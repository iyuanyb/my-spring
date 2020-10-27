package my_spring.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于从环境中（系统属性或 application.properties 配置文件）获取值，并赋给被标记的字段。
 * 仅可以用来标记简单类型（基本类型、包装类型、String）
 *
 * 格式：keyName:defaultValue，:defaultValue 可选。
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Value {
    String value();
}
