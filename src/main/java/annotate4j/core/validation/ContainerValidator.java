package annotate4j.core.validation;

import annotate4j.core.bin.annotation.ContainerSize;
import annotate4j.core.bin.annotation.FieldOrder;
import annotate4j.core.bin.utils.AnnotationHelper;
import annotate4j.core.bin.utils.FieldsBuilder;
import annotate4j.core.validation.exceptions.*;

import java.lang.reflect.Field;
import java.util.Set;

/**
 * @author Eugene Savin
 */
public class ContainerValidator extends FieldValidator {

    public ContainerValidator(Field field) {
        super(field);
    }

    @Override
    public void validate(Class clazz) throws ValidationException {
        ContainerSize cs = AnnotationHelper.getAnnotationOrNull(clazz, field, ContainerSize.class);
        if (cs == null) {
            return;
        }
        if (cs.value() > 0 && cs.fieldName().length() != 0) {
            throw new AmbiguousContainerSizeValueException(clazz, field, cs);
        }
        if (cs.value() > 0) {
            return;
        }
        String fieldName = cs.fieldName();
        boolean hasField = false;

        Set<Field> fields = FieldsBuilder.buildFields(clazz);
        Field ff = null;
        for (Field f : fields) {
            if (f.getName().equals(fieldName)) {
                hasField = true;
                ff = f;
                break;
            }
        }
        if (!hasField) {
            throw new MissingContainerSizeFieldException(clazz, field, fieldName);
        }
        Class type = ff.getType();
        if (!(Number.class.isAssignableFrom(type) || type.equals(int.class) ||
                type.equals(long.class) || type.equals(byte.class)
                || type.equals(short.class))) {
            throw new WrongContainerSizeFieldTypeException(clazz, field, fieldName, type.getName());
        }
        FieldOrder csAnnotation = ff.getAnnotation(FieldOrder.class);
        FieldOrder containerAnnotation = field.getAnnotation(FieldOrder.class);
        if (csAnnotation.index() > containerAnnotation.index()) {
            throw new CounterFieldNotInitializedException(clazz, field, ff);
        }


    }
}
