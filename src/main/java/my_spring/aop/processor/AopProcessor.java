package my_spring.aop.processor;

import my_spring.aop.annotation.Around;
import my_spring.aop.annotation.Aspect;
import my_spring.aop.annotation.Pointcut;
import my_spring.aop.aspect.AspectInfo;
import my_spring.core.processor.Processor;
import my_spring.core.container.BeanDefinition;
import my_spring.core.exception.InvalidBeanDefinitionException;
import my_spring.util.ReflectUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.stream.Collectors;

public class AopProcessor implements Processor {

    private static final Processor PROCESSOR = new AopProcessor();

    public static Processor getProcessor() {
        return PROCESSOR;
    }

    private final List<AspectInfo> aspectInfoList =
            beanContainer.getBeanDefinitionsByAnnotation(Aspect.class)
                    .stream()
                    .map(BeanDefinition::getBean)
                    .sorted((a, b) -> {
                        Aspect aspectA = a.getClass().getAnnotation(Aspect.class);
                        Aspect aspectB = b.getClass().getAnnotation(Aspect.class);
                        return aspectA.order() - aspectB.order();
                    })
                    .map(AopProcessor::createAspectInfo)
                    .collect(Collectors.toList());

    @Override
    public Object process(Object bean) {
        Class<?> clazz = bean.getClass();
        if (ReflectUtils.isAnnotatedWith(clazz, Aspect.class)) {
            return bean;
        }
        for (AspectInfo aspectInfo : aspectInfoList) {
            if (aspectInfo.match(clazz)) {
                bean = enhance(bean, aspectInfo);
            }
        }
        return bean;
    }

    /**
     * 对 bean 进行增强。
     */
    private static Object enhance(Object bean, AspectInfo aspectInfo) {
        return Proxy.newProxyInstance(AopProcessor.class.getClassLoader(), bean.getClass().getInterfaces(), new AopProxy(bean, aspectInfo));
    }

    /**
     * 根据切面对象创建 AspectInfo 对象
     *
     * @param aspect 切面对象
     */
    private static AspectInfo createAspectInfo(Object aspect) {
        Class<?> clazz = aspect.getClass();
        String pointcut = null;
        Method aroundMethod = null;

        for (Method method : clazz.getMethods()) {
            if (pointcut == null && method.isAnnotationPresent(Pointcut.class)) {
                pointcut = method.getAnnotation(Pointcut.class).value();
            }
            if (aroundMethod == null && method.isAnnotationPresent(Around.class)) {
                aroundMethod = method;
            }
        }
        if (pointcut == null) {
            throw new InvalidBeanDefinitionException(String.format("Aspect class %s missing annotation @Pointcut.", clazz.getName()));
        }
        if (aroundMethod == null) {
            throw new InvalidBeanDefinitionException(String.format("Aspect class %s missing annotation @Pointcut.", clazz.getName()));
        }
        return AspectInfo.create(aspect, pointcut, aroundMethod);
    }
}
