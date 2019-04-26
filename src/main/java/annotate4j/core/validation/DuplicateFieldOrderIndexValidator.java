package annotate4j.core.validation;

import annotate4j.core.HasInheritor;
import annotate4j.core.bin.annotation.FieldOrder;
import annotate4j.core.bin.utils.AnnotationHelper;
import annotate4j.core.bin.utils.ReflectionHelper;
import annotate4j.core.validation.exceptions.DuplicateFieldOrderIndexException;
import annotate4j.core.validation.exceptions.ValidationException;
import annotate4j.core.validation.exceptions.ValidationIllegalAccessException;
import annotate4j.core.validation.exceptions.ValidationInstantiationException;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Eugene Savin
 */
public class DuplicateFieldOrderIndexValidator implements Validator {

    private static Logger log = Logger.getLogger(DuplicateFieldOrderIndexValidator.class.getName());

    private Set<Integer> orderIndexSet = new HashSet<Integer>();

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


    public DuplicateFieldOrderIndexValidator() {

    }

    public DuplicateFieldOrderIndexValidator(Set<Class> processedSwitches, Set<Class> processedTypes) {
        this.processedSwitches = processedSwitches;
        this.processedTypes = processedTypes;
    }

    @Override
    public void validate(Class clazz) throws ValidationException {
        validate(clazz, orderIndexSet);
    }

    private void validate(Class clazz, Set<Integer> set) throws ValidationException {
        Class superClass = clazz.getSuperclass();
        if (superClass != null && !(superClass.equals(Object.class))) {
            validate(superClass, set);
        }
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            FieldOrder order = AnnotationHelper.getAnnotationOrNull(clazz, field, FieldOrder.class);
            if (order != null) {
                if (set.contains(order.index())) {
                    throw new DuplicateFieldOrderIndexException(clazz.getName(), field.getName(), order.index());
                } else {
                    set.add(order.index());
                }
            }
            Class fieldType = field.getType();
            if (List.class.isAssignableFrom(fieldType)) {
                fieldType = ((Class) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0]);
            }
            if (fieldType.isArray()) {
                fieldType = fieldType.getComponentType();
            }
            if (!processedTypes.contains(fieldType)) {
                processedTypes.add(fieldType);
                if (!systemTypes.contains(fieldType)) {
                    DuplicateFieldOrderIndexValidator v = new DuplicateFieldOrderIndexValidator(processedSwitches, processedTypes);
                    if (log.isLoggable(Level.FINE)) {
                        log.fine("processing class: " + clazz.getName() + ", fieldType: " + fieldType.getName() + ", field: " + field.getName());
                    }
                    v.validate(fieldType);

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
                        DuplicateFieldOrderIndexValidator v = new DuplicateFieldOrderIndexValidator(processedSwitches, processedTypes);
                        if (log.isLoggable(Level.FINE)) {
                            log.fine("processing class: " + c.getName() + " from " + clazz.getName());
                        }
                        v.validate(c);
                    }
                }
            }
        }

    }
}
