package annotate4j.core.bin.exceptions;

/**
 * Throws if the getter method absent for ContainerSize.fieldName()
 *
 * @author Eugene Savin
 */
public class WrongContainerSizeFieldException extends FieldReadException {
    private String containerName;
    private String counterFieldName;

    public WrongContainerSizeFieldException(String containerName, String counterFieldName) {
        this.containerName = containerName;
        this.counterFieldName = counterFieldName;
    }

    public String getContainerName() {
        return containerName;
    }

    public String getCounterFieldName() {
        return counterFieldName;
    }

    /**
     * Returns the detail message string of this throwable.
     *
     * @return the detail message string of this <tt>Throwable</tt> instance
     *         (which may be <tt>null</tt>).
     */
    @Override
    public String getMessage() {
        return "The getter method absent for container " + containerName + ", ContainerSize.fieldName " + counterFieldName;
    }
}
