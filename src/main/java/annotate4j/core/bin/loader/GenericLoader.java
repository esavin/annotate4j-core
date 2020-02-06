package annotate4j.core.bin.loader;

import annotate4j.core.Loader;
import annotate4j.core.bin.annotation.ContainerSize;
import annotate4j.core.bin.annotation.LittleEndian;
import annotate4j.core.bin.annotation.SkipBytes;
import annotate4j.core.bin.annotation.StringTerminator;
import annotate4j.core.bin.exceptions.*;
import annotate4j.core.bin.utils.AnnotationHelper;
import annotate4j.core.bin.utils.ClassSwitcher;
import annotate4j.core.bin.utils.ClassSwitcherImpl;
import annotate4j.core.bin.utils.ReflectionHelper;

import java.io.DataInput;
import java.io.EOFException;
import java.io.IOException;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Eugene Savin
 */
public abstract class GenericLoader implements Loader {

    protected DataInput in;
    protected Object instance = null;
    Object parent = null;
    Map<String, Object> injectedVariable;
    boolean isNeedInjection = false;
    ClassSwitcher cs = new ClassSwitcherImpl();
    protected static final Map<Field, Method> methodsByField = new HashMap<>();
    protected static final Map<Field, SkipBytes> skipBytesForField = new HashMap<>();

    boolean readClassInstance(Field f) throws FieldReadException {
        Object o = readClassInstance(f.getType());
        Method m;
        try {
            m = ReflectionHelper.getSetter(instance.getClass(), f.getName(), f.getType());
            m.invoke(instance, o);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new FieldReadException("Can not call setter for " + instance.getClass().getName() +
                    " class, field " + f.getName(), e);
        }
        return true;
    }

    boolean readContainer(Field f) throws IOException, FieldReadException {
        if (f.getType().equals(List.class)) {
            return readList(f);
        } else if (f.getType().isArray()) {
            return readArray(f);
        } else if (f.getType().equals(String.class)) {
            return readString(f);
        } else if (f.getType().equals(Map.class)) {
            return readMap(f);
        }
        return false;
    }

    private String readNullTerminatedString() throws FieldReadException {
        try {
            byte b;
            StringBuilder builder = new StringBuilder();
            while ((b = readByte()) != 0) {
                builder.append((char) b);
            }
            return builder.toString();
        } catch (IOException e) {
            throw new FieldReadException("Can not read bytes for null-terminated string", e);
        }
    }

    private boolean readString(Field f) throws
            FieldReadException, IOException {
        String result = null;

        StringTerminator t = null;
        try {
            t = AnnotationHelper.getAnnotation(instance.getClass(), f, StringTerminator.class);
        } catch (AnnotationNotSpecifiedException ee) {
            ContainerSize cs;
            try {
                cs = AnnotationHelper.getAnnotation(instance.getClass(), f, ContainerSize.class);
            } catch (AnnotationNotSpecifiedException eee) {
            }
        }

        byte terminator = 0;

        if (t != null) {
            terminator = t.value();
        }
        if (t != null || (t == null && cs == null)) {
            byte b;
            StringBuilder builder = new StringBuilder();
            while ((b = readByte()) != terminator) {
                builder.append((char) b);
            }
            result = builder.toString();
        }


        if (result == null) {
            long containerSize = getContainerSize(f);

            if (containerSize != 0) {
                Object obj = readByteArray(containerSize);
                result = new String((byte[]) obj);
            }
        }


        if (result != null) {
            try {
                Method m = methodsByField.get(f);
                if (m == null) {
                    m = ReflectionHelper.getSetter(instance.getClass(), f.getName(), f.getType());
                    methodsByField.put(f, m);
                }
                m.invoke(instance, result);
                return true;
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                throw new FieldReadException("Can not receive setter for " + instance.getClass().getName() +
                        " class, field " + f.getName());
            }
        }

        return false;

    }

    private boolean readArray(Field f) throws
            FieldReadException, IOException {

        long containerSize = getContainerSize(f);

        Class elementType = f.getType().getComponentType();
        Object obj;

        if (elementType.equals(int.class)) {
            obj = readIntArray(containerSize);
        } else if (elementType.equals(byte.class)) {
            obj = readByteArray(containerSize);
        } else if (elementType.equals(short.class)) {
            obj = readShortArray(containerSize);
        } else if (elementType.equals(long.class)) {
            obj = readLongArray(containerSize);
        } else {
            Object[] objectArray = (Object[]) Array.newInstance(elementType, (int) containerSize);
            for (int i = 0; i < containerSize; i++) {
                objectArray[i] = readContainerEntry(elementType, f);
            }
            obj = objectArray;
        }
        try {
            Method m = ReflectionHelper.getSetter(instance.getClass(), f.getName(), f.getType());
            m.invoke(instance, obj);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new FieldReadException("Can not receive setter for " + instance.getClass().getName() +
                    " class, field " + f.getName());
        }
        return true;
    }

    private byte[] readByteArray(long containerSize) throws IOException {
        byte[] bArray = new byte[(int) containerSize];
        for (int l = 0; l < containerSize; l++) {
            int i = in.readUnsignedByte();
            bArray[l] = (byte) i;
        }
        return bArray;
    }

    private long[] readLongArray(long containerSize) throws IOException {
        long[] lArray = new long[(int) containerSize];
        for (int l = 0; l < containerSize; l++) {
            long i = in.readLong();
            lArray[l] = i;
        }
        return lArray;
    }

    private short[] readShortArray(long containerSize) throws IOException {
        short[] sArray = new short[(int) containerSize];
        for (int l = 0; l < containerSize; l++) {
            int i = in.readUnsignedShort();

            sArray[l] = (short) i;
        }
        return sArray;
    }

    private int[] readIntArray(long containerSize) throws IOException {
        int[] iArray = new int[(int) containerSize];
        for (int l = 0; l < containerSize; l++) {
            int i = in.readInt();

            iArray[l] = i;
        }
        return iArray;
    }

    private Object readClassInstance(Class clazz) throws FieldReadException {
        Object obj;
        try {
            obj = clazz.newInstance();
            if (isNeedInjection) {
                try {
                    InputStreamLoader.injectVariable(obj, injectedVariable);
                } catch (ResolveException e) {
                    e.printStackTrace();
                }
            }
        } catch (InstantiationException | IllegalAccessException e) {
            throw new FieldReadException("Can not instantiate new instance of " + clazz.getName() + " class", e);
        }


        GenericLoader loader;
        try {
            loader = (GenericLoader) this.clone();
            loader.setInstance(obj);
        } catch (CloneNotSupportedException e) {
            throw new FieldReadException("Can not call clone() method");
        }

        return loader.load();
    }

    protected long getContainerSize(Field f) throws
            FieldReadException {

        ContainerSize cs;
        try {
            cs = AnnotationHelper.getAnnotation(instance.getClass(), f, ContainerSize.class);
        } catch (AnnotationNotSpecifiedException ee) {
            return Long.MAX_VALUE;
            //throw new UnspecifiedContainerSizeException(f.getName(), instance.getClass().getName());
        }

        if (cs.value() != -1) {
            return cs.value();
        }

        long corrector = cs.corrector();
        String counterFieldName = cs.fieldName();

        Method getter;
        try {
            getter = ReflectionHelper.getGetter(instance.getClass(), counterFieldName);
        } catch (NoSuchMethodException e) {
            throw new WrongContainerSizeFieldException(f.getName(), counterFieldName);
        }

        Object o = null;
        try {
            o = getter.invoke(instance);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new FieldReadException("Can not invoke getter " + getter.getName() + ", for class " +
                    instance.getClass().getName(), e);
        }
        if (o instanceof Number) {
            Number n = (Number) o;
            long l = n.longValue() + corrector;
            if (l < 0 && l > -256) {
                return (byte) l;
            } else {
                return n.longValue() + corrector;
            }
        }
        throw new WrongContainerSizeFieldTypeException(f.getName(), o.getClass().getName());
    }

    protected int getSkipBytes(Field f) throws FieldReadException {

        if (!skipBytesForField.containsKey(f)) {
            try {

                skipBytesForField.put(f, AnnotationHelper.getAnnotation(instance.getClass(), f, SkipBytes.class));
            } catch (AnnotationNotSpecifiedException ee) {
                skipBytesForField.put(f, null);
                return 0;
            }
        }

        SkipBytes sb = skipBytesForField.get(f);
        if (sb == null) {
            return 0;
        }
        if (sb.value() != 0) {
            return sb.value();
        }

        String counterFieldName = sb.fieldName();
        if (counterFieldName == null || counterFieldName.length() == 0) {
            return 0;
        }

        Method getter;
        try {
            getter = ReflectionHelper.getGetter(instance.getClass(), counterFieldName);
        } catch (NoSuchMethodException e) {
            return 0;
        }

        Object o;

        try {
            o = getter.invoke(instance);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new FieldReadException("Can not invoke getter for field " + f.getName() + ", method "
                    + getter.getName(), e);
        }

        if (o instanceof Number) {
            Number n = (Number) o;
            int l = n.intValue();
            if (l < 0 && l > -256) {
                return (byte) l;
            } else {
                return n.intValue();
            }
        }
        return 0;
    }


    Number readNumber(Class fieldType, boolean bigEndian) throws FieldReadException {
        try {
            if (fieldType.equals(long.class) || fieldType.equals(Long.class)) {
                long l = readLong();
                if (!bigEndian) {
                    l = Long.reverseBytes(l);
                }
                return l;
            }

            if (fieldType.equals(int.class) || fieldType.equals(Integer.class)) {
                int i = readInt();
                if (!bigEndian) {
                    i = Integer.reverseBytes(i);
                }
                return i;
            }

            if (fieldType.equals(short.class) || fieldType.equals(Short.class)) {
                short s = readShort();
                if (!bigEndian) {
                    s = Short.reverseBytes(s);
                }
                return s;
            }

            if (fieldType.equals(byte.class) || fieldType.equals(Byte.class)) {
                return readByte();
            }
        } catch (IOException e) {
            throw new FieldReadException("Can not read field type " + fieldType.getName(), e);
        }
        return null;
    }

    protected boolean readMap(Field f) throws FieldReadException {

        Type t = f.getGenericType();
        Class keyClass = ((Class) ((ParameterizedType) t).getActualTypeArguments()[0]);
        Class valueClass = ((Class) ((ParameterizedType) t).getActualTypeArguments()[1]);
        long containerSize = getContainerSize(f);
        Map map = null;
        boolean newMap = false;
        try {
            Method getter = ReflectionHelper.getGetter(instance.getClass(), f.getName());
            Object obj = getter.invoke(instance);
            if (obj instanceof Map) {
                map = (Map) obj;
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new FieldReadException("Can not call getter for " + instance.getClass().getName() +
                    " class, field " + f.getName());
        }
        if (map == null) {
            map = new HashMap();
            newMap = true;
        }
        long l = 0;
        Object key = null;
        Object value = null;
        try {

            while (l < containerSize) {
                key = readContainerEntry(keyClass, null);
                value = readContainerEntry(valueClass, null);
                map.put(key, value);
                key = null;
                value = null;
                l++;
            }
        } catch (FieldReadException fre) {
            if (fre.getCause() instanceof EOFException && Long.MAX_VALUE == containerSize) {
                if (fre.getPartialCreatedInstance() != null) {
                    if (key != null && value != null) {
                        map.put(key, value);
                    } else if (key != null) {
                        map.put(key, fre.getPartialCreatedInstance());
                    }
                }
            } else {
                throw fre;
            }
        }


        if (newMap) {
            try {
                Method m = ReflectionHelper.getSetter(instance.getClass(), f.getName(), List.class);
                m.invoke(instance, map);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                throw new FieldReadException("Can not call setter for " + instance.getClass().getName() +
                        " class, field " + f.getName());
            }
        }
        return true;

    }

    protected boolean readList(Field f) throws FieldReadException {
        Type t = f.getGenericType();
        Class clazz = ((Class) ((ParameterizedType) t).getActualTypeArguments()[0]);
        long containerSize = getContainerSize(f);
        List list = null;
        boolean newList = false;
        try {
            Method getter = ReflectionHelper.getGetter(instance.getClass(), f.getName());
            Object obj = getter.invoke(instance);
            if (obj instanceof List) {
                list = (List) obj;
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new FieldReadException("Can not call getter for " + instance.getClass().getName() +
                    " class, field " + f.getName(), e);
        }
        if (list == null) {
            list = new ArrayList();
            newList = true;
        }
        long l = 0;
        try {
            while (l < containerSize) {
                Object o = readContainerEntry(clazz, f);
                list.add(o);
                l++;
            }
        } catch (FieldReadException fre) {
            if (fre.getCause() instanceof EOFException && Long.MAX_VALUE == containerSize) {
                if (fre.getPartialCreatedInstance() != null) {
                    list.add(fre.getPartialCreatedInstance());
                }
            } else {
                throw fre;
            }
        }


        if (newList) {
            try {
                Method m = ReflectionHelper.getSetter(instance.getClass(), f.getName(), List.class);
                m.invoke(instance, list);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                throw new FieldReadException("Can not call setter for " + instance.getClass().getName() +
                        " class, field " + f.getName(), e);
            }
        }
        return true;

    }

    protected Object readContainerEntry(Class clazz, Field f) throws FieldReadException {
        Object o;
        if (clazz.equals(int.class) || clazz.equals(Integer.class) ||
                clazz.equals(short.class) || clazz.equals(Short.class) ||
                clazz.equals(byte.class) || clazz.equals(Byte.class) ||
                clazz.equals(long.class) || clazz.equals(Long.class)) {
            boolean bigEndian = true;
            if (f != null && f.getDeclaredAnnotation(LittleEndian.class) != null) {
                bigEndian = false;
            }
            o = readNumber(clazz, bigEndian);

        } else if (clazz.equals(String.class)) {
            o = readNullTerminatedString();
        } else {
            o = readClassInstance(clazz);
        }
        return o;
    }

    private Long readLong() throws IOException {
        return in.readLong();

    }

    private Integer readInt() throws IOException {
        return in.readInt();

    }

    private Short readShort() throws IOException {
        return (short) in.readUnsignedShort();

    }

    private Byte readByte() throws IOException {
        return (byte) in.readUnsignedByte();

    }

    public Object load(Object inheritor, Object superClass) throws FieldReadException {
        this.parent = superClass;
        setInstance(inheritor);
        return load();
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        InputStreamLoader loader;
        if (isNeedInjection) {
            loader = new InputStreamLoader(in, injectedVariable);
        } else {
            loader = new InputStreamLoader(in, null);
        }
        loader.setClassSwitcher(cs);

        return loader;
    }

    public void setClassSwitcher(ClassSwitcher cs) {
        this.cs = cs;
    }

    protected void setInstance(Object instance) {
        this.instance = instance;
    }

}
