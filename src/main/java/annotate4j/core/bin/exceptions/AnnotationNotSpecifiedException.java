package annotate4j.core.bin.exceptions;

/**
 * @author Eugene Savin
 */
public class AnnotationNotSpecifiedException extends FieldReadException {

    private String annotationName;
    private String fieldName;
    private String className;

    public AnnotationNotSpecifiedException(String annotationName, String fieldName, String className) {
        this.annotationName = annotationName;
        this.fieldName = fieldName;
        this.className = className;
    }

    public String getAnnotationName() {
        return annotationName;
    }

    public String getFieldName() {
        return fieldName;
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
        return "The annotation " + getAnnotationName() + " does not specified for `" + getFieldName() + "` field or for it setter in class " + getClassName();
    }
}
