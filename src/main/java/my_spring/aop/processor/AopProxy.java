package my_spring.aop.processor;

import my_spring.aop.annotation.Around;
import my_spring.aop.annotation.Aspect;
import my_spring.aop.annotation.Pointcut;
import my_spring.aop.aspect.AspectInfo;
import my_spring.aop.aspect.JoinPoint;
import my_spring.core.exception.InvalidBeanDefinitionException;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

public class AopProxy implements InvocationHandler {

    private final AspectInfo aspectInfo;
    private final Object bean;

    public AopProxy(Object bean, AspectInfo aspectInfo) {
        this.aspectInfo = aspectInfo;
        this.bean = bean;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // method 方法是否需要增强
        String pointcut = aspectInfo.getPointcut();
        if (pointcut.charAt(pointcut.length() - 1) != '*' &&
                !method.getName().equals(pointcut.substring(pointcut.lastIndexOf('.') + 1))) {
            return method.invoke(bean, args);
        }
        return aspectInfo.getAroundMethod().invoke(aspectInfo.getAspect(),
                buildAroundMethodArgs(bean, method, args, aspectInfo.getAroundMethod()));
    }

    private static final Map<Method, Map<String, Integer>> argNameToIndexCache = new HashMap<>();

    private static Object[] buildAroundMethodArgs(Object bean, Method realMethod, Object[] realMethodArgs, Method aroundMethod) {
        Object[] args = new Object[aroundMethod.getParameterCount()];
        Parameter[] parameters = aroundMethod.getParameters();

        // 双重检查锁
        if (!argNameToIndexCache.containsKey(realMethod)) {
            synchronized (argNameToIndexCache) {
                if (!argNameToIndexCache.containsKey(realMethod)) {
                    Map<String, Integer> argNameToIndex = new HashMap<>();
                    Parameter[] realMethodParameters = realMethod.getParameters();
                    for (int i = 0; i < realMethodParameters.length; ++i) {
                        argNameToIndex.put(realMethodParameters[i].getName(), i);
                    }
                    argNameToIndexCache.put(realMethod, argNameToIndex);
                }
            }
        }

        Map<String, Integer> argNameToIndex = argNameToIndexCache.get(realMethod);
        for (int i = 0; i < parameters.length; ++i) {
            if (parameters[i].getType() == JoinPoint.class) {
                args[i] = JoinPoint.create(bean, realMethod, realMethodArgs);
            } else {
                args[i] = realMethodArgs[argNameToIndex.get(parameters[i].getName())];
            }
        }

        return args;
    }

}
