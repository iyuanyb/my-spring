package my_spring.core.exception;

/**
 * 类定义错误异常
 */
public class InvalidClassDefinitionException extends RuntimeException {
    public InvalidClassDefinitionException() {
    }

    public InvalidClassDefinitionException(Throwable e) {
        super(e);
    }

    public InvalidClassDefinitionException(String message) {
        super(message);
    }

    public InvalidClassDefinitionException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
