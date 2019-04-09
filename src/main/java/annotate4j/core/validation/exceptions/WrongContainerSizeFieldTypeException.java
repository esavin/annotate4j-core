package annotate4j.core.validation.exceptions;

import java.lang.reflect.Field;

/**
 * @author Eugene Savin
 */
public class WrongContainerSizeFieldTypeException extends ValidationException {

    private Class clazz;
    private Field field;
    private String containerSizeFieldName;
    private String containerSizeFieldType;

    public WrongContainerSizeFieldTypeException(Class clazz, Field field, String containerSizeFieldName, String containerSizeFieldType) {
        this.clazz = clazz;
        this.containerSizeFieldName = containerSizeFieldName;
        this.containerSizeFieldType = containerSizeFieldType;
        this.field = field;
    }

    public Class getClazz() {
        return clazz;
    }

    public String getContainerSizeFieldName() {
        return containerSizeFieldName;
    }

    public String getContainerSizeFieldType() {
        return containerSizeFieldType;
    }

    public Field getField() {
        return field;
    }

    @Override
    public String getMessage() {
        return "Wrong type " + containerSizeFieldType + " of field " + containerSizeFieldName +
                " declared as container size field for " + field.getName() +
                " container in class " + clazz.getName();
    }
}
