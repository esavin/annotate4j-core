package annotate4j.core.validation.exceptions;

/**
 * @author Eugene Savin
 */
public class ValidationInstantiationException extends ValidationException {
    private Class clazz;
    private Throwable throwable;

    public ValidationInstantiationException(Class clazz, Throwable throwable) {
        super("Can not instantiate class " + clazz.getName(), throwable);
        this.clazz = clazz;
        this.throwable = throwable;
    }

    public Class getClazz() {
        return clazz;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    @Override
    public String getMessage() {
        return "Can not instantiate class " + clazz.getName() + ", cause: it is possible the class an abstract or interface";
    }
}
