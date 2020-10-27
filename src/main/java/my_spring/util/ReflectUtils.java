package my_spring.util;


import my_spring.core.annotation.Component;
import my_spring.core.common.Constant;
import my_spring.core.exception.InvalidClassDefinitionException;
import my_spring.core.exception.NoSuchMethodException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class ReflectUtils {

    private ReflectUtils() {
        throw new UnsupportedOperationException();
    }

    /**
     * 某个类是否被 @Component 注解或被 @Component 注解标记的注解标记了。
     */
    public static boolean isAnnotatedWithComponent(Class<?> clazz) {
        return isAnnotatedWith(clazz, Component.class);
    }


    /**
     * 某个类是否被 annotationType 类型的注解或被 annotationType 类型的注解标记的注解标记了，
     * 也就是支持了组合注解。
     *
     * @param clazz 要判断的类的 Class 对象
     * @param annotationType 要判断的注解的 Class 对象
     */
    public static boolean isAnnotatedWith(Class<?> clazz, Class<? extends Annotation> annotationType) {
        for (Annotation annotation : clazz.getAnnotations()) {
            // 被标记直接返回
            // notice: 刚开始用自定义类加载器加载所有类，导致条件为false，
            // 因为annotationType是由AppClassLoader加载的。
            if (annotation.annotationType() == annotationType) {
                return true;
            }
            // 否则递归处理（只分析自定义注解，否则会无限递归）
            if (!annotation.toString().startsWith(Constant.JAVA_ANNOTATION_PKG_PREFIX)) {
                if (isAnnotatedWith(annotation.annotationType(), annotationType)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 根据参数 clazz 新创建一个对象
     */
    public static <T> T newInstance(Class<T> clazz) {
        try {
            Constructor<T> constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (Exception e) {
            throw new InvalidClassDefinitionException(e);
        }
    }


    /**
     * 是否是简单类型。
     */
    public static boolean isSimpleType(Class<?> clazz) {
        return clazz == String.class || clazz.isPrimitive();
    }


    /**
     * 反射方法缓存，getMethod 很耗时。
     */
    private static final Map<String, Method> methodCache = new HashMap<>();

    /**
     * 调用对象 obj 的名为 methodName 的方法
     * @param <T> 方法返回类型
     */
    @SuppressWarnings("unchecked")
    public static <T> T invokeMethod(Object obj, String methodName, Object... args) {
        Class<?> clazz = obj.getClass();
        String methodKey = buildMethodKey(obj, methodName, args);
        try {
            // 双重检查锁
            if (!methodCache.containsKey(methodKey)) {
                synchronized (methodCache) {
                    if (!methodCache.containsKey(methodKey)) {
                        Class<?>[] argsClasses = new Class<?>[args.length];
                        for (int i = 0; i < args.length; i++) {
                            argsClasses[i] = args[i].getClass();
                        }
                        methodCache.put(methodKey, clazz.getMethod(methodName, argsClasses));
                    }
                }
            }
            return (T)methodCache.get(methodKey).invoke(obj, args);
        } catch (Exception e) {
            throw new NoSuchMethodException(e);
        }
    }

    /**
     * 构建方法缓存map的key
     */
    private static String buildMethodKey(Object obj, String methodName, Object... args) {
        // 不是给人看的
        StringBuilder sb = new StringBuilder();
        sb.append(obj.getClass().getName())
                .append(methodName);
        for (Object arg : args) {
            sb.append(arg.getClass().getName());
        }
        return sb.toString();
    }


    public static void setField(Object obj, Field field, Object val) {
        field.setAccessible(true);
        try {
            field.set(obj, val);
        } catch (IllegalAccessException ignore) {
        }
    }

    /**
     * String -> 基本类型 转换
     */
    private static final Map<Class<?>, Function<String, Object>> typeConverter = new HashMap<>();
    static {
        typeConverter.put(String.class, v -> v);
        typeConverter.put(char.class, str -> str.charAt(0));
        typeConverter.put(Character.class, str -> str.charAt(0));
        typeConverter.put(int.class, Integer::valueOf);
        typeConverter.put(Integer.class, Integer::valueOf);
        typeConverter.put(long.class, Long::valueOf);
        typeConverter.put(Long.class, Long::valueOf);
        typeConverter.put(double.class, Double::valueOf);
        typeConverter.put(Double.class, Double::valueOf);
        typeConverter.put(float.class, Float::valueOf);
        typeConverter.put(Float.class, Float::valueOf);
        typeConverter.put(byte.class, Byte::valueOf);
        typeConverter.put(Byte.class, Byte::valueOf);
        typeConverter.put(short.class, Short::valueOf);
        typeConverter.put(Short.class, Short::valueOf);
        typeConverter.put(boolean.class, Boolean::valueOf);
        typeConverter.put(Boolean.class, Boolean::valueOf);
    }
    public static Object cast(String val, Class<?> toClass) {
        try {
            return typeConverter.get(toClass).apply(val);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return null;
        }
    }
}


