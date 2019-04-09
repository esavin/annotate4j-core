package annotate4j.core.exceptions;

/**
 * @author Eugene Savin
 */
public class ConvertationException extends Exception {
    public ConvertationException() {
    }

    public ConvertationException(String message) {
        super(message);
    }

    public ConvertationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConvertationException(Throwable cause) {
        super(cause);
    }
}
