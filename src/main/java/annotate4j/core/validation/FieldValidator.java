package annotate4j.core.validation;

import java.lang.reflect.Field;

/**
 * @author Eugene Savin
 */
public abstract class FieldValidator implements Validator {
    protected Field field;

    public FieldValidator(Field field) {
        this.field = field;
    }

}
