package annotate4j.core.validation.exceptions;

import java.lang.reflect.Field;

/**
 * @author Eugene Savin
 */
public class SetterNotFoundException extends ValidationException {

    private Class clazz;

    private Field field;

    public SetterNotFoundException(Class clazz, Field field) {
        this.clazz = clazz;
        this.field = field;
    }

    public Class getClazz() {
        return clazz;
    }

    public Field getField() {
        return field;
    }

    @Override
    public String getMessage() {
        return "Can not found public setter for field " + field.getName() + " in class " + clazz.getName();
    }
}
