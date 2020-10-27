package my_spring.core.exception;

/**
 * bean 不存在异常。
 */
public class BeanNotFoundException extends RuntimeException {
    public BeanNotFoundException() {
    }

    public BeanNotFoundException(Throwable e) {
        super(e);
    }

    public BeanNotFoundException(String message) {
        super(message);
    }

    public BeanNotFoundException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
