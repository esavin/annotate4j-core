package annotate4j.core.validation;

import annotate4j.core.bin.utils.ReflectionHelper;
import annotate4j.core.validation.exceptions.GetterNotFoundException;
import annotate4j.core.validation.exceptions.ValidationException;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author Eugene Savin
 */
public class GetterValidator extends FieldValidator {

    public GetterValidator(Field field) {
        super(field);
    }

    @Override
    public void validate(Class clazz) throws ValidationException {
        Method m;
        try {
            m = ReflectionHelper.getGetter(clazz, field.getName());

        } catch (NoSuchMethodException e) {
            throw new GetterNotFoundException(clazz, field);
        }
        if (!m.getReturnType().equals(field.getType())) {
            throw new GetterNotFoundException(clazz, field);
        }
        int mod = m.getModifiers();
        if ((mod | 1) != mod) {
            throw new GetterNotFoundException(clazz, field);
        }
    }
}
