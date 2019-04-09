package annotate4j.core.bin.utils;

import annotate4j.core.bin.exceptions.AnnotationNotSpecifiedException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * @author Eugene Savin
 */
public class AnnotationHelper {

    public static <T extends Annotation> T getClassAnnotationOrNull(String className, Class<T> annotationClass) {
        try {
            Class c = Class.forName(className);
            return getClassAnnotationOrNull(c, annotationClass);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    public static <T extends Annotation> T getClassAnnotationOrNull(Class<?> c, Class<T> annotationClass) {
        if (c == null) {
            return null;
        }
        T annotation = c.getAnnotation(annotationClass);
        if (annotation != null) {
            return annotation;
        }
        if (c.getName().equals("java.lang.Object")) {
            return null;
        }
        return getClassAnnotationOrNull(c.getSuperclass(), annotationClass);

    }

    public static <T extends Annotation> T getAnnotation(Class<?> c, Field field, Class<T> annotationClass) throws AnnotationNotSpecifiedException {
        T t = getAnnotationOrNull(c, field, annotationClass);
        if (t == null) {
            throw new AnnotationNotSpecifiedException(annotationClass.getName(), field.getName(), c.getName());
        }
        return t;
    }

    public static <T extends Annotation> T getAnnotationOrNull(Class<?> c, Field field, Class<T> annotationClass) {
        T t;
        // COMMENT: the current implementation does not work with annotated methods
        /*try {
            Method setter = ReflectionHelper.getSetter(c, field.getName(), field.getType());
            t = setter.getAnnotation(annotationClass);
            if (t != null) {
                return t;
            }
        } catch (NoSuchMethodException e1) {
            //skip it
        }*/

        t = field.getAnnotation(annotationClass);
        return t;
    }

}
