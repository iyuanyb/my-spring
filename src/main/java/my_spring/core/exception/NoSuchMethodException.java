package my_spring.core.exception;

public class NoSuchMethodException extends RuntimeException{
    public NoSuchMethodException() {
    }

    public NoSuchMethodException(Throwable e) {
        super(e);
    }

    public NoSuchMethodException(String message) {
        super(message);
    }

    public NoSuchMethodException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
