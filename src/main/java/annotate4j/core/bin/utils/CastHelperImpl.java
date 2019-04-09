package annotate4j.core.bin.utils;

import annotate4j.core.annotation.FieldOrder;
import annotate4j.core.bin.exceptions.ClassesNotInSameHierarchyException;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author Eugene Savin
 */
public class CastHelperImpl implements CastHelper {

    /**
     * Copy all fields value from parent object to inheritor
     *
     * @param parent    parent object
     * @param inheritor inherit object
     * @return inheritor type instance
     * @throws ClassesNotInSameHierarchyException
     *          if the objects not in same hierarchy
     */
    public Object cast(Object parent, Object inheritor) throws ClassesNotInSameHierarchyException {
        if (parent == inheritor) {
            return parent;
        }
        if (!parent.getClass().isInstance(inheritor)) {
            throw new ClassesNotInSameHierarchyException(parent.getClass().getName(),
                    inheritor.getClass().getName());
        }

        Field[] fields = parent.getClass().getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            try {
                if (fields[i].getAnnotation(FieldOrder.class) == null) {
                    continue;
                }
                Method parentGetter = ReflectionHelper.getGetter(parent.getClass(), fields[i].getName());
                Method childSetter = ReflectionHelper.getSetter(inheritor.getClass(), fields[i].getName(), fields[i].getType());
                Object obj = parentGetter.invoke(parent);
                childSetter.invoke(inheritor, obj);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();       // TODO: add specific exception
            } catch (IllegalAccessException e1) {
                e1.printStackTrace();      // TODO: add specific exception
            } catch (InvocationTargetException e2) {
                e2.printStackTrace();        // TODO: add specific exception
            }

        }


        return inheritor;
    }
}
