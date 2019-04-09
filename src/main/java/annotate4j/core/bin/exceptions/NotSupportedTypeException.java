package annotate4j.core.bin.exceptions;

/**
 * @author Eugene Savin
 */
public class NotSupportedTypeException extends FieldReadException {
    private String className;

    public NotSupportedTypeException(String className) {
        this.className = className;
    }

    public String getClassName() {
        return className;
    }


    /**
     * Returns the detail message string of this throwable.
     *
     * @return the detail message string of this <tt>Throwable</tt> instance
     *         (which may be <tt>null</tt>).
     */
    @Override
    public String getMessage() {
        return "The type `" + getClassName() + "` does not supported";
    }
}
