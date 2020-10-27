package my_spring.core.container;

import my_spring.core.exception.InvalidBeanDefinitionException;
import my_spring.util.ProcessorUtils;
import my_spring.util.ReflectUtils;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;


/**
 * Bean 容器。
 */
public class BeanContainer {
    /** 单例 Bean 容器 */
    private static final BeanContainer INSTANCE = new BeanContainer();

    /** beanName -> BeanDefinition*/
    private final Map<String, BeanDefinition> beanMap = new ConcurrentHashMap<>();

    /** beanType -> beanName */
    private final Map<Class<?>, List<String>> typeToName = new ConcurrentHashMap<>();

    /** 环境信息 */
    private Environment environment;


    /**
     * 获取单例 bean 容器
     */
    public static BeanContainer getBeanContainer() {
        return BeanContainer.INSTANCE;
    }

    /**
     * 添加一个 bean。
     */
    public synchronized void addBeanDefinition(BeanDefinition beanDefinition) {
        String beanName = beanDefinition.getBeanName();
        Class<?> beanClass = beanDefinition.getType();
        if (beanMap.containsKey(beanName)) {
            throw new InvalidBeanDefinitionException("BeanName " + beanName + "already exists.");
        }
        beanMap.put(beanName, beanDefinition);
        typeToName.putIfAbsent(beanClass, new ArrayList<>());
        typeToName.get(beanClass).add(beanName);
    }

    /**
     * 移除一个 bean。
     */
    public boolean removeBeanDefinition(String beanName) {
        if (this.beanMap.containsKey(beanName)) {
            this.typeToName.remove(this.beanMap.get(beanName).getType());
            this.beanMap.remove(beanName);
            return true;
        }
        return false;
    }

    /**
     * 获取所有的 BeanDefinition。
     */
    public Collection<BeanDefinition> getAllBeanDefinitions() {
        return beanMap.values();
    }

    /**
     * 根据 beanName 从容器中获取 BeanDefinition
     */
    public BeanDefinition getBeanDefinition(String beanName) {
        return beanMap.get(beanName);
    }


    /**
     * 判断容器中是否包含指定 Bean
     */
    public boolean containsBeanDefinition(String beanName) {
        return beanMap.containsKey(beanName);
    }


    /**
     * BeanContainer 中的元素个数。
     */
    public int getBeanDefinitionsCount() {
        return beanMap.size();
    }


    /**
     * 获取被 annotationClass 的类型的注解标记的 BeanDefinition。
     */
    public List<BeanDefinition> getBeanDefinitionsByAnnotation(Class<? extends Annotation> annotationClass) {
        return typeToName.keySet()
                .stream()
                .filter(clazz -> ReflectUtils.isAnnotatedWith(clazz, annotationClass))
                .flatMap(clazz -> typeToName.get(clazz).stream().map(beanMap::get))
                .collect(Collectors.toList());
    }


    /**
     * 获取对应类型及其子类的 BeanDefinition
     */
    public List<BeanDefinition> getBeanDefinitionsBySuper(Class<?> superClassOrInterface) {
         return typeToName.keySet()
                .stream()
                .filter(superClassOrInterface::isAssignableFrom)
                .flatMap(clazz -> typeToName.get(clazz).stream().map(beanMap::get))
                .collect(Collectors.toList());
    }

    public Environment getEnvironment() {
        return environment;
    }

    public void setEnvironment(Environment environment) {
        if (this.environment != null) {
            return;
        }
        this.environment = environment;
    }

    public void executeProcessors() {
        beanMap.forEach((name, beanDefinition) -> {
            beanDefinition.setBean(ProcessorUtils.process(beanDefinition.getBean()));
        });
    }
}
