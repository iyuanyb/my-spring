package my_spring.core.processor;

import my_spring.core.annotation.Value;
import my_spring.core.container.Environment;
import my_spring.core.exception.InvalidBeanDefinitionException;
import my_spring.util.ReflectUtils;

import java.lang.reflect.Field;

public class ValueProcessor implements Processor {

    private static final Processor PROCESSOR = new ValueProcessor();

    private ValueProcessor() {
    }

    public static Processor getProcessor() {
        return PROCESSOR;
    }

    @Override
    public Object process(Object bean) {
        Environment environment = beanContainer.getEnvironment();
        for (Field field : bean.getClass().getDeclaredFields()) {
            if (!field.isAnnotationPresent(Value.class)) {
                continue;
            }
            Value annotation = field.getAnnotation(Value.class);
            String info = annotation.value();
            if (info.indexOf(':') != -1) {
                String[] arr = info.split(":");
                String val = environment.getProperty(arr[0]);
                val = (val == null ? arr[1] : val);
                ReflectUtils.setField(bean, field, ReflectUtils.cast(val, field.getType()));
            } else {
                String val = environment.getProperty(info);
                if (val == null) {
                    throw new InvalidBeanDefinitionException(String.format("Value key[%s] not found.", info));
                }
                ReflectUtils.setField(bean, field, ReflectUtils.cast(val, field.getType()));
            }
        }
        return bean;
    }
}
