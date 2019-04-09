package annotate4j.core.bin.exceptions;

/**
 * @author Eugene Savin
 */
public class ResolvedClassNotFoundException extends ResolveException {
    private String className;
    private String key;

    public ResolvedClassNotFoundException(String className, String key) {
        this.className = className;
        this.key = key;
    }

    public String getClassName() {
        return className;
    }

    public String getKey() {
        return key;
    }

    /**
     * Returns the detail message string of this throwable.
     *
     * @return the detail message string of this <tt>Throwable</tt> instance
     *         (which may be <tt>null</tt>).
     */
    @Override
    public String getMessage() {
        return "Can not found switched class by key `" + getKey() + "`, class `"
                + getClassName() + "`";
    }
}
