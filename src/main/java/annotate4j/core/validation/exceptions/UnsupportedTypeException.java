package annotate4j.core.validation.exceptions;

/**
 * @author Eugene Savin
 */
public class UnsupportedTypeException extends ValidationException {

    private String typeName = null;

    private String fieldName = null;

    private String className;


    public UnsupportedTypeException(String className, String fieldName, String typeName) {
        this.className = className;
        this.fieldName = fieldName;
        this.typeName = typeName;
    }

    public UnsupportedTypeException(String className) {
        this.className = className;
    }

    public String getClassName() {
        return className;
    }

    public String getTypeName() {
        return typeName;
    }

    public String getFieldName() {
        return fieldName;
    }

    @Override
    public String getMessage() {
        return "Unsupported type " + typeName + " for field " + fieldName + " in class " + className;

    }
}
