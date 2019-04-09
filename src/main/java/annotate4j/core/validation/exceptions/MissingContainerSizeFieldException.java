package annotate4j.core.validation.exceptions;

import java.lang.reflect.Field;

/**
 * @author Eugene Savin
 */
public class MissingContainerSizeFieldException extends ValidationException {

    private Class clazz;
    private Field field;
    private String containerSizeFieldName;

    public MissingContainerSizeFieldException(Class clazz, Field field, String containerSizeFieldName) {
        this.clazz = clazz;
        this.containerSizeFieldName = containerSizeFieldName;
        this.field = field;
    }

    public Class getClazz() {
        return clazz;
    }

    public String getContainerSizeFieldName() {
        return containerSizeFieldName;
    }

    public Field getField() {
        return field;
    }

    @Override
    public String getMessage() {
        return "Missing field " + containerSizeFieldName + " declared as container size field for " + field.getName() +
                " container in class " + clazz.getName();
    }
}
