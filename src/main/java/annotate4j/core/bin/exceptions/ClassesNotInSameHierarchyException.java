package annotate4j.core.bin.exceptions;

/**
 * @author Eugene Savin
 */
public class ClassesNotInSameHierarchyException extends FieldReadException {
    private String parentClassName;

    private String inheritorClassName;

    public ClassesNotInSameHierarchyException(String parentClassName, String inheritorClassName) {
        this.parentClassName = parentClassName;
        this.inheritorClassName = inheritorClassName;
    }

    public String getInheritorClassName() {
        return inheritorClassName;
    }

    public String getParentClassName() {
        return parentClassName;
    }

    /**
     * Returns the detail message string of this throwable.
     *
     * @return the detail message string of this <tt>Throwable</tt> instance
     *         (which may be <tt>null</tt>).
     */
    @Override
    public String getMessage() {
        return "The class `" + getInheritorClassName() + "` does not inherit `" + getParentClassName() + "`";
    }
}
