package annotate4j.core.validation.exceptions;

/**
 * @author Eugene Savin
 */
public class MissingOrderedFieldsException extends ValidationException {

    private MissingOrderedFieldsException savedException = null;

    private Class clazz;

    public MissingOrderedFieldsException(Class clazz, MissingOrderedFieldsException savedException) {
        this.clazz = clazz;
        this.savedException = savedException;
    }

    public MissingOrderedFieldsException(Class clazz) {
        this.clazz = clazz;
    }

    public Class getClazz() {
        return clazz;
    }

    public MissingOrderedFieldsException getSavedException() {
        return savedException;
    }
}
