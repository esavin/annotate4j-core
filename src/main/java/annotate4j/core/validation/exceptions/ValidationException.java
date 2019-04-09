package annotate4j.core.validation.exceptions;

/**
 * @author Eugene Savin
 */
public class ValidationException extends Exception {

    public ValidationException() {
    }

    public ValidationException(Throwable cause) {
        super(cause);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }

}
