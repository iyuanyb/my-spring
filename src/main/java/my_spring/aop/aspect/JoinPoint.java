package my_spring.aop.aspect;

import java.lang.reflect.Method;

/**
 * 连接点。
 */
public class JoinPoint {

    private Object bean;

    private Method method;

    private Object[] args;

    private JoinPoint() {}

    public static JoinPoint create(Object bean, Method method, Object[] args) {
        JoinPoint joinPoint = new JoinPoint();
        joinPoint.bean = bean;
        joinPoint.method = method;
        joinPoint.args = args;
        return joinPoint;
    }

    public Object proceed() throws Throwable {
        return method.invoke(bean, args);
    }
}
