package my_spring.core.container;

import my_spring.util.ProcessorUtils;
import my_spring.util.ReflectUtils;

import java.lang.reflect.Method;

/**
 * Bean 的定义。
 */
public class BeanDefinition {

    /** Bean 对象 */
    private Object bean;

    /** Bean 的名字 */
    private String beanName;

    /** Bean 对应的 Class */
    private Class<?> type;

    /** 被 @Bean 标记的方法，用来创建 Bean 实例，如果不为 null 则表示这种方式创建 Bean */
    private Method beanCreatorMethod;

    /** 调用 beanCreatorMethod 需要用到 */
    private Object configurationObject;

    /** Bean 的作用域 */
    private BeanScope scope;

    /** 是否延时初始化，仅当 @{code scope == SINGLETON} 时有意义 */
    private boolean lazyInit = false;

    /** 初始化方法方法 */
    private String initMethod;

    /** 销毁方法方法 */
    private String destroyMethod;


    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public BeanScope getScope() {
        return scope;
    }

    public void setScope(BeanScope scope) {
        this.scope = scope;
    }

    public boolean isLazyInit() {
        return lazyInit;
    }

    public void setLazyInit(boolean lazyInit) {
        this.lazyInit = lazyInit;
    }

    public Object getBean() {
        // SINGLETON and (not lazyInit)
        if (bean != null) {
            return bean;
        }
        // @Component
        if (beanCreatorMethod == null) {
            // PROTOTYPE
            if (scope == BeanScope.PROTOTYPE) {
                Object bean = ReflectUtils.newInstance(type);
                ProcessorUtils.process(bean);
                return bean;
            }
            // lazyInit
            else if (lazyInit) {
                this.bean = ReflectUtils.newInstance(type);
                ProcessorUtils.process(this.bean);
                return bean;
            } else {
                // not implemented
            }
        }
        // @Bean
        else {
            // PROTOTYPE
            if (scope == BeanScope.PROTOTYPE) {
                Object bean = BeanCreator.createBeanByCreatorMethod(this);
                ProcessorUtils.process(bean);
                return bean;
            }
            // lazyInit
            else if (lazyInit) {
                this.bean = BeanCreator.createBeanByCreatorMethod(this);
                ProcessorUtils.process(this.bean);
                return bean;
            } else {
                // not implemented
            }
        }
        return null;
    }

    public void setBean(Object bean) {
        this.bean = bean;
    }

    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }

    public void invokeInitMethod() {
        ReflectUtils.invokeMethod(bean, initMethod);
    }

    public void setInitMethod(String initMethod) {
        this.initMethod = initMethod;
    }

    public void invokeDestroyMethod() {
        ReflectUtils.invokeMethod(bean, destroyMethod);
    }

    public void setDestroyMethod(String destroyMethod) {
        this.destroyMethod = destroyMethod;
    }

    public Method getBeanCreatorMethod() {
        return beanCreatorMethod;
    }

    public void setBeanCreatorMethod(Method beanCreatorMethod) {
        this.beanCreatorMethod = beanCreatorMethod;
        this.beanCreatorMethod.setAccessible(true);
    }

    public Object getConfigurationObject() {
        return configurationObject;
    }

    public void setConfigurationObject(Object configurationObject) {
        this.configurationObject = configurationObject;
    }

    @Override
    public String toString() {
        return "BeanDefinition{" +
                "bean=" + bean +
                ", beanName='" + beanName + '\'' +
                ", beanClass=" + type +
                ", beanCreatorMethod=" + beanCreatorMethod +
                ", configurationObject=" + configurationObject +
                ", scope=" + scope +
                ", lazyInit=" + lazyInit +
                ", initMethod='" + initMethod + '\'' +
                ", destroyMethod='" + destroyMethod + '\'' +
                '}';
    }
}
