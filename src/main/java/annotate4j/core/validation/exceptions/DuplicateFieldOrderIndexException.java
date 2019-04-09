package annotate4j.core.validation.exceptions;

/**
 * @author Eugene Savin
 */
public class DuplicateFieldOrderIndexException extends ValidationException {
    private String className;
    private String fieldName;
    private long index;

    public DuplicateFieldOrderIndexException(String className, String fieldName, long index) {
        this.className = className;
        this.fieldName = fieldName;
        this.index = index;
    }

    public String getClassName() {
        return className;
    }

    public String getFieldName() {
        return fieldName;
    }

    public long getIndex() {
        return index;
    }

    @Override
    public String getMessage() {
        return "There are duplicate index " + index + " for field " + fieldName + " in class " + className;
    }
}
