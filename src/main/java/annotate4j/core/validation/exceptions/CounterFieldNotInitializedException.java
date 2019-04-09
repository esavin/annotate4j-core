package annotate4j.core.validation.exceptions;

import java.lang.reflect.Field;

/**
 * @author Eugene Savin
 */
public class CounterFieldNotInitializedException extends ValidationException {

    private Class clazz;
    private Field containerField;
    private Field containerSizeField;

    public CounterFieldNotInitializedException(Class clazz, Field containerField, Field containerSizeField) {
        this.clazz = clazz;
        this.containerField = containerField;
        this.containerSizeField = containerSizeField;
    }

    public Class getClazz() {
        return clazz;
    }

    public Field getContainerField() {
        return containerField;
    }

    public Field getContainerSizeField() {
        return containerSizeField;
    }

    @Override
    public String getMessage() {
        return "For container " + containerField.getName() + " in class " + clazz.getName() +
                " container size field " + containerSizeField.getName() + " declared after container field";

    }
}
