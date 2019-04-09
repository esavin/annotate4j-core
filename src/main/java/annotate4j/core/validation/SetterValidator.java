package annotate4j.core.validation;

import annotate4j.core.bin.utils.ReflectionHelper;
import annotate4j.core.validation.exceptions.SetterNotFoundException;
import annotate4j.core.validation.exceptions.ValidationException;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author Eugene Savin
 */
public class SetterValidator extends FieldValidator {

    public SetterValidator(Field field) {
        super(field);
    }

    @Override
    public void validate(Class clazz) throws ValidationException {
        Method m;
        try {
            m = ReflectionHelper.getSetter(clazz, field.getName(), field.getType());
        } catch (NoSuchMethodException e) {
            throw new SetterNotFoundException(clazz, field);
        }
        int mod = m.getModifiers();
        if ((mod | 1) != mod) {
            throw new SetterNotFoundException(clazz, field);
        }

    }
}
