package my_spring.util;

import my_spring.aop.processor.AopProcessor;
import my_spring.core.processor.*;

import java.util.ArrayList;
import java.util.List;

public class ProcessorUtils {

    private static final List<Processor> processors = new ArrayList<>();

    static {
        processors.add(BeanProcessor.getProcessor());
        processors.add(ValueProcessor.getProcessor());
        processors.add(DynamicValueProcessor.getProcessor());
        processors.add(AutowiredProcessor.getProcessor());
        // 在 autowired 时，如果某个 bean 需要 AOP 代理，则先 AOP 处理
        processors.add(AopProcessor.getProcessor()); // 这么遍历应用AOP，错误
    }

    private ProcessorUtils() {
        throw new UnsupportedOperationException();
    }

    public static Object process(Object bean) {
        if (bean == null) {
            return null;
        }
        for (Processor processor : processors) {
            bean = processor.process(bean);
        }
        return bean;
    }
}
