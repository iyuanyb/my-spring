package my_spring.aop.aspect;


import java.lang.reflect.Method;

public class AspectInfo {
    /** 调用 aroundMethod 时使用 */
    private Object aspect;

    private String pointcut;

    private Method aroundMethod;

    private AspectInfo() {}

    public static AspectInfo create(Object aspect, String pointcut, Method aroundMethod) {
        AspectInfo aspectInfo = new AspectInfo();
        aspectInfo.aspect = aspect;
        aspectInfo.pointcut = pointcut;
        aspectInfo.aroundMethod = aroundMethod;
        return aspectInfo;
    }


    /**
     * 当前切面是否要增强 clazz 类对应的对象。
     */
    public boolean match(Class<?> clazz) {
        // a.b.* 形式
        if (pointcut.charAt(pointcut.length() - 1) == '*') {
            return clazz.getName().startsWith(pointcut.substring(0, pointcut.length() - 2));
        }
        // a.b.method
        else {
            return clazz.getName().equals(pointcut.substring(0, pointcut.lastIndexOf('.')));
        }
    }

    public String getPointcut() {
        return pointcut;
    }

    public Method getAroundMethod() {
        return aroundMethod;
    }

    public Object getAspect() {
        return aspect;
    }
}
