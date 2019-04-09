package annotate4j.core.bin.exceptions;

/**
 * Throws if ContainerSize.fieldName() has not a Number type
 *
 * @author Eugene Savin
 */
public class WrongContainerSizeFieldTypeException extends FieldReadException {

    private String type;

    private String containerName;

    public WrongContainerSizeFieldTypeException(String containerName, String type) {
        this.type = type;
    }

    public String getContainerName() {
        return containerName;
    }

    public String getType() {
        return type;
    }

    /**
     * Returns the detail message string of this throwable.
     *
     * @return the detail message string of this <tt>Throwable</tt> instance
     *         (which may be <tt>null</tt>).
     */
    @Override
    public String getMessage() {
        return "A type " + type + " not a java.lang.Number. ContainerSize should be a Number for container " + getContainerName();
    }
}
