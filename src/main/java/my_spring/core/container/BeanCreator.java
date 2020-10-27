package my_spring.core.container;

import my_spring.core.annotation.*;
import my_spring.core.exception.InvalidBeanDefinitionException;
import my_spring.util.ReflectUtils;
import my_spring.util.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;


public class BeanCreator {

    /** Bean 容器 */
    private static final BeanContainer beanContainer = BeanContainer.getBeanContainer();

    private BeanCreator() {
        throw new UnsupportedOperationException();
    }

    public static void createBeanDefinition(Class<?> clazz) {
        BeanDefinition beanDefinition = new BeanDefinition();
        Annotation annotation;
        if (ReflectUtils.isAnnotatedWith(clazz, Service.class)) {
            annotation = clazz.getAnnotation(Service.class);
        } else if (ReflectUtils.isAnnotatedWith(clazz, Controller.class)) {
            annotation = clazz.getAnnotation(Controller.class);
        } else if (ReflectUtils.isAnnotatedWith(clazz, Repository.class)) {
            annotation = clazz.getAnnotation(Repository.class);
        } else { // @Component
            annotation = clazz.getAnnotation(Component.class);
        }
        initBeanDefinition(beanDefinition, clazz, annotation);
        // 实例化
        if (beanDefinition.getScope() == BeanScope.SINGLETON && !beanDefinition.isLazyInit()) {
            beanDefinition.setBean(ReflectUtils.newInstance(clazz));
        }
        beanContainer.addBeanDefinition(beanDefinition);
    }

    public static void createBeanDefinition(Object configurationObject, Method method) {
        BeanDefinition beanDefinition = new BeanDefinition();
        initBeanDefinition(beanDefinition, method.getReturnType(), method.getAnnotation(Bean.class));
        beanDefinition.setBeanCreatorMethod(method);
        beanDefinition.setConfigurationObject(configurationObject);
        // 实例化
        if (beanDefinition.getScope() == BeanScope.SINGLETON && !beanDefinition.isLazyInit()) {
            beanDefinition.setBean(createBeanByCreatorMethod(beanDefinition));
        }
        beanContainer.addBeanDefinition(beanDefinition);
    }

    /**
     * 给 BeanDefinition 设置一些通用属性。
     */
    private static void initBeanDefinition(BeanDefinition beanDefinition, Class<?> clazz, Annotation annotation) {
        if (annotation instanceof Component || annotation instanceof Service || annotation instanceof Bean ||
                annotation instanceof Controller || annotation instanceof Repository) {
            String name = ReflectUtils.invokeMethod(annotation, "name");
            beanDefinition.setBeanName(StringUtils.isEmpty(name) ?
                    StringUtils.getDefaultBeanName(clazz.getSimpleName()) : name);
            beanDefinition.setLazyInit(ReflectUtils.invokeMethod(annotation, "lazyInit"));
            beanDefinition.setScope(ReflectUtils.invokeMethod(annotation, "scope"));
            beanDefinition.setInitMethod(ReflectUtils.invokeMethod(annotation, "initMethod"));
            beanDefinition.setDestroyMethod(ReflectUtils.invokeMethod(annotation, "destroyMethod"));
            beanDefinition.setType(clazz);
        } else { // 其他 @Component 组合注解
            beanDefinition.setBeanName(StringUtils.getDefaultBeanName(clazz.getSimpleName()));
            beanDefinition.setScope(BeanScope.SINGLETON);
            beanDefinition.setType(clazz);
        }
    }

    /**
     * 使用被 @Bean 注解了的方法创建 Bean。
     */
    public static Object createBeanByCreatorMethod(BeanDefinition beanDefinition) {
        Object configurationObject = beanDefinition.getConfigurationObject();
        Method beanCreatorMethod = beanDefinition.getBeanCreatorMethod();
        Object[] args = new Object[beanCreatorMethod.getParameterCount()];
        Parameter[] parameters = beanCreatorMethod.getParameters();
        // @Bean 注解的方法的参数，仅支持 byType 注入
        for (int i = 0; i < args.length; ++i) {
            List<BeanDefinition> autowiredBeans = beanContainer.getBeanDefinitionsBySuper(parameters[i].getType());
            // 有一个匹配的类型
            if (autowiredBeans.size() == 1) {
                args[i] = autowiredBeans.get(0);
            }
            // 有多个匹配的类型，但可以根据 fieldName 获取到 Bean
            else if (beanContainer.getBeanDefinition(parameters[i].getName()) != null) {
                args[i] = beanContainer.getBeanDefinition(parameters[i].getName());
            }
            // 失败
            else {
                throw new InvalidBeanDefinitionException(
                        String.format("Multiple BeanDefinition with type %s was found.", parameters[i].getType().getName()));
            }
        }
        try {
            return beanCreatorMethod.invoke(configurationObject, args);
        } catch (Exception e) {
            throw new InvalidBeanDefinitionException(String.format("Creating bean %s failed.", beanDefinition.getBeanName()), e);
        }
    }
}

