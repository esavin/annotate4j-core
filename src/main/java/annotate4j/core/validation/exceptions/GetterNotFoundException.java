package annotate4j.core.validation.exceptions;

import java.lang.reflect.Field;

/**
 * @author Eugene Savin
 */
public class GetterNotFoundException extends ValidationException {

    private Class clazz;

    private Field field;

    public GetterNotFoundException(Class clazz, Field field) {
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
        return "Can not found public getter for field " + field.getName() + " in class " + clazz.getName();
    }
}
