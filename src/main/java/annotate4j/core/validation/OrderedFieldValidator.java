package annotate4j.core.validation;

import annotate4j.core.HasInheritor;
import annotate4j.core.bin.annotation.FieldOrder;
import annotate4j.core.bin.utils.AnnotationHelper;
import annotate4j.core.bin.utils.ReflectionHelper;
import annotate4j.core.validation.exceptions.UnsupportedTypeException;
import annotate4j.core.validation.exceptions.ValidationException;
import annotate4j.core.validation.exceptions.ValidationIllegalAccessException;
import annotate4j.core.validation.exceptions.ValidationInstantiationException;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implements the Visitor design pattern. Visit all fields annotated with <code>FieldOrder</code> annotation and
 * call for each field <code>internalValidate</code> method.
 * <p/>
 * Add your custom validators in <code>internalValidate</code> method.
 *
 * @author Eugene Savin
 */
public class OrderedFieldValidator implements Validator {

    private static Logger log = Logger.getLogger(DuplicateFieldOrderIndexValidator.class.getName());

    private Set<Class> processedSwitches = new HashSet<Class>();
    private Set<Class> processedTypes = new HashSet<Class>();
    private final static Set<Class> systemTypes = new HashSet<Class>();

    static {
        systemTypes.add(String.class);
        systemTypes.add(Short.class);
        systemTypes.add(Integer.class);
        systemTypes.add(Byte.class);
        systemTypes.add(Long.class);
    }


    @Override
    public void validate(Class clazz) throws ValidationException {
        Class superClass = clazz.getSuperclass();
        if (superClass != null && !(superClass.equals(Object.class))) {
            validate(superClass);
        }
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            FieldOrder order = AnnotationHelper.getAnnotationOrNull(clazz, field, FieldOrder.class);
            if (order != null) {
                internalValidate(clazz, field);
            }
            Class fieldType = field.getType();
            if (fieldType.isAssignableFrom(List.class)) {
                String genericType = field.getGenericType().toString();
                if (genericType.indexOf("<") == -1 || genericType.indexOf(">") == -1) {
                    throw new UnsupportedTypeException(clazz.getName(), field.getName(), fieldType.getName());
                }
                genericType = genericType.substring(genericType.indexOf("<") + 1, genericType.lastIndexOf(">"));
                try {
                    fieldType = Class.forName(genericType);
                } catch (ClassNotFoundException cnfe) {
                    throw new UnsupportedTypeException(clazz.getName(), field.getName(), genericType);
                }
            }
            if (!processedTypes.contains(fieldType)) {
                processedTypes.add(fieldType);
                if (!systemTypes.contains(fieldType)) {
                    if (log.isLoggable(Level.FINE)) {
                        log.fine("processing class: " + clazz.getName() + ", fieldType: " + fieldType.getName() + ", field: " + field.getName());
                    }
                    validate(fieldType);
                }
            }
        }

        if (ReflectionHelper.isDirectImplements(clazz, HasInheritor.class)) {
            if (!processedSwitches.contains(clazz)) {
                processedSwitches.add(clazz);

                HasInheritor<Class> hi;

                try {
                    hi = (HasInheritor<Class>) clazz.newInstance();
                } catch (InstantiationException e) {
                    throw new ValidationInstantiationException(clazz, e);
                } catch (IllegalAccessException e) {
                    throw new ValidationIllegalAccessException(clazz, e);
                }
                for (Class c : hi.getInheritors()) {
                    if (!processedTypes.contains(c)) {
                        processedTypes.add(c);
                        if (log.isLoggable(Level.FINE)) {
                            log.fine("processing class: " + c.getName() + " from " + clazz.getName());
                        }
                        validate(c);
                    }
                }
            }
        }
    }

    private void internalValidate(Class clazz, Field field) throws ValidationException {
        new SetterValidator(field).validate(clazz);
        new GetterValidator(field).validate(clazz);
        new ContainerValidator(field).validate(clazz);
    }
}
