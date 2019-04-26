package annotate4j.core.validation;

import annotate4j.core.HasInheritor;
import annotate4j.core.bin.annotation.FieldOrder;
import annotate4j.core.bin.utils.AnnotationHelper;
import annotate4j.core.bin.utils.ReflectionHelper;
import annotate4j.core.validation.exceptions.*;

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
public class SupportedTypeValidator implements Validator {
    private static Logger log = Logger.getLogger(SupportedTypeValidator.class.getName());

    private static Set<Class> processedClasses = new HashSet<Class>();
    private static Set<Class> supportedClasses = new HashSet<Class>();

    static {
        supportedClasses.add(int.class);
        supportedClasses.add(Integer.class);
        supportedClasses.add(short.class);
        supportedClasses.add(Short.class);
        supportedClasses.add(long.class);
        supportedClasses.add(Long.class);
        supportedClasses.add(byte.class);
        supportedClasses.add(Byte.class);
//        supportedClasses.add(String.class);
        supportedClasses.add(int[].class);
        supportedClasses.add(byte[].class);
        supportedClasses.add(long[].class);
        supportedClasses.add(short[].class);

    }


    @Override
    public void validate(Class clazz) throws ValidationException {
        if (processedClasses.contains(clazz)) {
            return;
        }
        processedClasses.add(clazz);
        boolean hasOrderedField = false;
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            FieldOrder order = AnnotationHelper.getAnnotationOrNull(clazz, field, FieldOrder.class);
            if (order != null) {
                if (!hasOrderedField) {
                    hasOrderedField = true;
                }
                Class fieldType = field.getType();
                if (supportedClasses.contains(fieldType)) {
                    continue;
                }
                if (List.class.isAssignableFrom(fieldType)) {
                    fieldType = ((Class) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0]);
                    if (supportedClasses.contains(fieldType)) {
                        continue;
                    }
                }
                if (fieldType.isArray()) {
                    fieldType = fieldType.getComponentType();
                    if (supportedClasses.contains(fieldType)) {
                        continue;
                    }
                }
                try {
                    if (log.isLoggable(Level.FINE)) {
                        log.fine("processing class: " + clazz.getName() + ", fieldType: " + fieldType.getName() + ", field: " + field.getName());
                    }
                    validate(fieldType);
                } catch (UnsupportedTypeException ute) {
                    if (ute.getTypeName() == null && ute.getFieldName() == null) {
                        throw new UnsupportedTypeException(clazz.getName(), field.getName(), ute.getClassName());
                    } else {
                        throw ute;
                    }
                } catch (MissingOrderedFieldsException mofe) {
                    throw new UnsupportedTypeException(clazz.getName(), field.getName(), mofe.getClazz().getName());

                }
            }
        }
        Class superClass = clazz.getSuperclass();
        if (superClass == null) {
            throw new UnsupportedTypeException(clazz.getName());
        }
        if (!(superClass.equals(Object.class))) {
            try {
                validate(superClass);
            } catch (UnsupportedTypeException ute) {
                throw ute;
            } catch (MissingOrderedFieldsException mofe) {
                if (!hasOrderedField) {
                    throw new MissingOrderedFieldsException(clazz, mofe);
                }
            }
        } else {
            if (!hasOrderedField) {
                throw new MissingOrderedFieldsException(clazz);
            }
        }

        if (!supportedClasses.contains(clazz)) {
            supportedClasses.add(clazz);

            if (ReflectionHelper.isDirectImplements(clazz, HasInheritor.class)) {
                HasInheritor<Class> hi;
                try {
                    hi = (HasInheritor<Class>) clazz.newInstance();
                } catch (InstantiationException e) {
                    throw new ValidationInstantiationException(clazz, e);
                } catch (IllegalAccessException e) {
                    throw new ValidationIllegalAccessException(clazz, e);
                }
                for (Class c : hi.getInheritors()) {
                    if (!supportedClasses.contains(c)) {
                        try {
                            Object obj = c.newInstance();
                            if (!clazz.isInstance(obj)) {
                                throw new InheritanceException(c, clazz);
                            }
                        } catch (InstantiationException e) {
                            throw new ValidationInstantiationException(c, e);
                        } catch (IllegalAccessException e) {
                            throw new ValidationIllegalAccessException(c, e);
                        }
                        if (log.isLoggable(Level.FINE)) {
                            log.fine("processing class: " + c.getName() + " from StructureSwitch " + clazz.getName());
                        }
                        validate(c);
                    }
                }
            }
        }

    }


}
