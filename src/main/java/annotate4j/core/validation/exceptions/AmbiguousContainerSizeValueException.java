package annotate4j.core.validation.exceptions;

import annotate4j.core.bin.annotation.ContainerSize;

import java.lang.reflect.Field;

/**
 * @author Eugene Savin
 */
public class AmbiguousContainerSizeValueException extends ValidationException {

    private Class clazz;
    private Field field;
    private String value;
    private String fieldName;

    public AmbiguousContainerSizeValueException(Class clazz, Field field, ContainerSize cs) {
        this.clazz = clazz;
        this.field = field;
        value = String.valueOf(cs.value());
        fieldName = cs.fieldName();
    }

    public Class getClazz() {
        return clazz;
    }

    public Field getField() {
        return field;
    }

    @Override
    public String getMessage() {
        return "For container " + field.getName() + " in class " + clazz.getName() + " declared concrete container size value " + value +
                " and field with container size variable " + fieldName;
    }
}
