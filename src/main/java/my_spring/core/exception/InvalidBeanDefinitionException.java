package my_spring.core.exception;

public class InvalidBeanDefinitionException extends RuntimeException {
    public InvalidBeanDefinitionException() {
    }

    public InvalidBeanDefinitionException(Throwable e) {
        super(e);
    }

    public InvalidBeanDefinitionException(String message) {
        super(message);
    }

    public InvalidBeanDefinitionException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
