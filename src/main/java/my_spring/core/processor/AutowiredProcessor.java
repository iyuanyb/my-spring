package my_spring.core.processor;

import my_spring.core.annotation.Autowired;
import my_spring.core.container.BeanDefinition;
import my_spring.core.exception.BeanNotFoundException;
import my_spring.core.exception.InvalidBeanDefinitionException;
import my_spring.util.ReflectUtils;
import my_spring.util.StringUtils;

import java.lang.reflect.Field;
import java.util.List;

public class AutowiredProcessor implements Processor {

    private static final Processor PROCESSOR = new AutowiredProcessor();

    private AutowiredProcessor() {
    }

    public static Processor getProcessor() {
        return PROCESSOR;
    }

    @Override
    public Object process(Object bean) {
        if (!ReflectUtils.isAnnotatedWithComponent(bean.getClass())) {
             return bean;
        }
        for (Field field : bean.getClass().getDeclaredFields()) {
            if (!field.isAnnotationPresent(Autowired.class)) {
                continue;
            }
            Autowired autowired = field.getAnnotation(Autowired.class);
            String name = autowired.name();
            // byType
            if (StringUtils.isEmpty(name)) {
                List<BeanDefinition> autowiredBeans = beanContainer.getBeanDefinitionsBySuper(field.getType());
                // 有一个匹配的类型
                if (autowiredBeans.size() == 0 && autowired.required()) {
                    throw new BeanNotFoundException(String.format("Not found the bean that %s:%s need.", bean.getClass(), field.getName()));
                } if (autowiredBeans.size() == 1) {
                    ReflectUtils.setField(bean, field, autowiredBeans.get(0).getBean());
                }
                // 有多个匹配的类型，但可以根据 fieldName 获取到 Bean
                else if (beanContainer.getBeanDefinition(field.getName()) != null) {
                    ReflectUtils.setField(bean, field, beanContainer.getBeanDefinition(field.getName()));
                }
                // 失败
                else {
                    throw new InvalidBeanDefinitionException(
                            String.format("Multiple BeanDefinition with type %s was found.", field.getType().getName()));
                }
            }
            // byName
            else {
                BeanDefinition autowiredBeanDefinition = beanContainer.getBeanDefinition(name);
                if (autowiredBeanDefinition != null) {
                    ReflectUtils.setField(bean, field, autowiredBeanDefinition.getBean());
                } else {
                    // not found
                    throw new InvalidBeanDefinitionException(
                            String.format("BeanDefinition with name %s not found.", name));
                }
            }
        }
        return bean;
    }
}
