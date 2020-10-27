package my_spring.core.exception;


/**
 * 应用启动时异常。
 */
public class AppStartUpException extends RuntimeException {

    public AppStartUpException() {
    }

    public AppStartUpException(Throwable e) {
        super(e);
    }

    public AppStartUpException(String message) {
        super(message);
    }

    public AppStartUpException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
