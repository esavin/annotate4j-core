package annotate4j.core.validation;

import annotate4j.core.validation.exceptions.ValidationException;

/**
 * Validator provides mechanism to ensure that the validated class has a proper structure:
 * <ul>
 * <li>
 * </li>
 * </ul>
 *
 * @author Eugene Savin
 */
public interface Validator {

    public void validate(Class clazz) throws ValidationException;

}
