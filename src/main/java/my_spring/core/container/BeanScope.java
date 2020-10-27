package my_spring.core.container;

/**
 * Bean 的作用域
 */
public enum BeanScope {
    /** 单例 */
    SINGLETON,
    /** 原型 */
    PROTOTYPE,
    /** 请求 */
    REQUEST,
    /** 会话 */
    SESSION
}
