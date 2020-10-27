package my_spring.core.processor;

import my_spring.core.annotation.Bean;
import my_spring.core.annotation.Condition;
import my_spring.core.annotation.Conditional;
import my_spring.core.annotation.Configuration;
import my_spring.core.container.BeanCreator;
import my_spring.core.exception.InvalidBeanDefinitionException;
import my_spring.util.ReflectUtils;

import java.lang.reflect.Method;

/**
 * 处理 @Configuration 注解标记的类
 */
public class BeanProcessor implements Processor {

    private static final Processor PROCESSOR = new BeanProcessor();

    private BeanProcessor() {
    }

    public static Processor getProcessor() {
        return PROCESSOR;
    }

    @Override
    public Object process(Object bean) {
        if (!ReflectUtils.isAnnotatedWith(bean.getClass(), Configuration.class)) {
            return bean;
        }
        for (Method method : bean.getClass().getMethods()) {
            if (!method.isAnnotationPresent(Bean.class)) {
                continue;
            }
            if (method.isAnnotationPresent(Conditional.class)) {
                Conditional conditional = method.getAnnotation(Conditional.class);
                Class<?> conditionClass = conditional.conditionClass();
                if (!Condition.class.isAssignableFrom(conditionClass)) {
                    throw new InvalidBeanDefinitionException("Class " + conditionClass + " doesn't implement the Condition interface.");
                }
                // @Conditional
                else if (!((Condition)(ReflectUtils.newInstance(conditionClass))).test(beanContainer)) {
                    continue;
                }
            }
            BeanCreator.createBeanDefinition(bean, method);
        }
        return bean;
    }
}
