package annotate4j.core.bin.utils;

import annotate4j.core.bin.annotation.FieldOrder;
import annotate4j.core.bin.exceptions.AnnotationNotSpecifiedException;
import annotate4j.core.bin.exceptions.OrderNotSpecifiedException;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author Eugene Savin
 */
public class ReflectionHelper {


    public Method getSetter(Class<?> c, Field f) throws NoSuchMethodException {
        String methodName = getSetterName(f.getName());
        return c.getMethod(methodName, f.getType());
    }

    public static Method getGetter(Class<?> c, Field f) throws NoSuchMethodException {
        String methodName = getGetterName(f.getName());
        return c.getMethod(methodName);
    }

    public static String firstCharToUpper(String s) {
        if (s == null || s.length() < 1) {
            return new String();
        }
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    public static Method getGetter(Class<?> c, String fieldName) throws NoSuchMethodException {
        String methodName = getGetterName(fieldName);
        return c.getMethod(methodName);
    }

    public static Object invokeGetter(Object obj, String fieldName) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String methodName = getGetterName(fieldName);
        Method m = obj.getClass().getMethod(methodName);
        return m.invoke(obj);
    }

    public static Method getSetter(Class<?> c, String fieldName, Class<?>... parameterTypes) throws NoSuchMethodException {
        String methodName = getSetterName(fieldName);
        return c.getMethod(methodName, parameterTypes);
    }

    public static void invokeSetter(Object instance, String setterName, Object value, Class<?>... parameterTypes) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method m = ReflectionHelper.getSetter(instance.getClass(), setterName, parameterTypes);
        m.invoke(instance, value);
    }

    public static void invokeSetter(Object instance, Field field, Object value, Class<?>... parameterTypes) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String setterName = ReflectionHelper.getSetterName(field.getName());
        invokeSetter(instance, setterName, value, parameterTypes);
    }

    public static String getGetterName(String fieldName) {
        return getJavaBeanName(fieldName, "get");
    }

    public static String getSetterName(String fieldName) {
        return getJavaBeanName(fieldName, "set");
    }

    public static String getJavaBeanName(String fieldName, String prefix) {
        return prefix + firstCharToUpper(fieldName);
    }

    public static int getOrder(Class<?> c, Field field) throws OrderNotSpecifiedException {
        try {
            FieldOrder fieldOrder = AnnotationHelper.getAnnotation(c, field, FieldOrder.class);
            return fieldOrder.index();
        } catch (AnnotationNotSpecifiedException e) {
            throw new OrderNotSpecifiedException(field.getName(), c.getName());
        }
    }

    public static int getOrder(Class<?> c, String fieldName) throws OrderNotSpecifiedException {
        Field field = null;
        try {

            field = c.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            //skip it
        }
        if (field == null && !c.getName().equals("java.lang.Object")) {
            return getOrder(c.getSuperclass(), fieldName);
        } else if (c.getName().equals("java.lang.Object")) {
            throw new OrderNotSpecifiedException(fieldName, c.getName());
        }

        try {
            FieldOrder fieldOrder = AnnotationHelper.getAnnotation(c, field, FieldOrder.class);
            return fieldOrder.index();
        } catch (AnnotationNotSpecifiedException e) {
            throw new OrderNotSpecifiedException(fieldName, c.getName());
        }
    }

    public static boolean isDirectImplements(Class clazz, Class _interface) {
        Class[] interfaces = clazz.getInterfaces();
        for (Class i : interfaces) {
            if (i.equals(_interface)) {
                return true;
            }
        }
        return false;
    }

}
