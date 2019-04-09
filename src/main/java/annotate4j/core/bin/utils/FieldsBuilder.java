package annotate4j.core.bin.utils;

import annotate4j.core.annotation.FieldOrder;
import annotate4j.core.bin.exceptions.OrderNotSpecifiedException;

import java.lang.reflect.Field;
import java.util.*;

/**
 * @author Eugene Savin
 */
public class FieldsBuilder {

    private static Map<ClassPair, Field[]> fieldCash = new Hashtable<ClassPair, Field[]>();

    public static Field[] buildFields(Object instance, Object parent) {

        String className;
        String parentClassName = parent.getClass().getName();
        if (instance instanceof Class) {
            className = ((Class) instance).getName();
        } else {
            className = instance.getClass().getName();
        }
        ClassPair cp = new ClassPair(className, parentClassName);
        if (fieldCash.containsKey(cp)) {
            return fieldCash.get(cp);
        }

        Object superClass;
        Field[] fs;
        if (instance instanceof Class) {
            if (((Class) instance).getName().equals(parent.getClass().getCanonicalName())) {
                fieldCash.put(cp, new Field[0]);
                return new Field[0];
            }
            Class cl = (Class) instance;
            fs = cl.getDeclaredFields();
            superClass = cl.getGenericSuperclass();
        } else {
            superClass = instance.getClass().getSuperclass();
            fs = instance.getClass().getDeclaredFields();
        }

        Field[] parentFields = FieldsBuilder.buildFields(superClass, parent);
        int fsLength = 0;
        if (fs != null) {
            fsLength = fs.length;
        }
        Field[] ff = new Field[fsLength + parentFields.length];
        int k;
        for (k = 0; k < parentFields.length; k++) {
            ff[k] = parentFields[k];
        }

        if (fs != null) {
            for (int j = 0; j < fsLength; j++) {
                ff[k] = fs[j];
                k++;
            }
        }
        if (ff.length < 1) {
            fieldCash.put(cp, new Field[0]);
            return new Field[0];
        }
        Map<Integer, Field> m = new TreeMap<Integer, Field>();
        for (int i = 0; i < ff.length; i++) {
            Field f = ff[i];
            try {
                int index = ReflectionHelper.getOrder(instance.getClass(), f);
                m.put(index, f);
            } catch (OrderNotSpecifiedException e) {
                //skip it. does not working with this field
            }
        }
        Field[] fields = new Field[m.size()];
        Set<Integer> keys = m.keySet();
        Iterator<Integer> iter = keys.iterator();
        int i = 0;
        while (iter.hasNext()) {
            Integer key = iter.next();
            Field f = m.get(key);
            fields[i] = f;
            i++;
        }
        fieldCash.put(cp, fields);
        return fields;
    }

    public static Set<Field> buildFields(Class clazz) {
        Set<Field> s = new HashSet<Field>();
        if (!clazz.getSuperclass().equals(Object.class)) {
            s.addAll(buildFields(clazz.getSuperclass()));
        }
        for (Field f : clazz.getDeclaredFields()) {
            if (f.getAnnotation(FieldOrder.class) != null)
                s.add(f);
        }
        return s;
    }


}
