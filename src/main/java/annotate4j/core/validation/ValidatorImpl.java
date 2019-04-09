package annotate4j.core.validation;

import annotate4j.core.validation.exceptions.ValidationException;

/**
 * @author Eugene Savin
 */
public class ValidatorImpl implements Validator {

    @Override
    public void validate(Class clazz) throws ValidationException {
        new SupportedTypeValidator().validate(clazz);
        new DuplicateFieldOrderIndexValidator().validate(clazz);
        new OrderedFieldValidator().validate(clazz);
    }
}
