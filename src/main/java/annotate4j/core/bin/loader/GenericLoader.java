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
    protected static final Map<Field, SkipBytesHolder> skipBytesForField = new HashMap<>();
    private static final SkipBytesHolder noSkip = new SkipBytesHolder();

    boolean readClassInstance(Field f) throws FieldReadException,
            IllegalAccessException, InvocationTargetException {
        Object o = readClassInstance(f.getType());
        Method m;
        try {
            m = ReflectionHelper.getSetter(instance.getClass(), f.getName(), f.getType());
            m.invoke(instance, o);
        } catch (NoSuchMethodException e) {
            throw new FieldReadException("Can not receive setter for " + instance.getClass().getName() +
                    " class, field " + f.getName());
        }
        return true;
    }


    boolean readContainer(Field f) throws IOException,
            IllegalAccessException, InvocationTargetException,
            FieldReadException {
        if (f.getType().equals(List.class)) {
            return readList(f);
        } else if (f.getType().isArray()) {
            return readArray(f);
        } else if (f.getType().equals(String.class)) {
            return readString(f);
        }
        return false;
    }

    private boolean readString(Field f) throws IllegalAccessException, InvocationTargetException,
            FieldReadException, IOException {
        String result = null;
        try {
            StringTerminator t = AnnotationHelper.getAnnotation(instance.getClass(), f, StringTerminator.class);
            if (t != null) {
                byte terminator = t.value();
                byte b;
                StringBuilder builder = new StringBuilder();
                while ((b = readByte()) != terminator) {
                    builder.append((char) b);
                }
                result = builder.toString();
            }
        } catch (AnnotationNotSpecifiedException e) {
            //skip it
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
            } catch (NoSuchMethodException e) {
                throw new FieldReadException("Can not receive setter for " + instance.getClass().getName() +
                        " class, field " + f.getName());
            }
        }

        return false;

    }

    private boolean readArray(Field f) throws IllegalAccessException,
            InvocationTargetException,
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
        } catch (NoSuchMethodException e) {
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

    private Object readClassInstance(Class clazz) throws FieldReadException, IllegalAccessException {
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
        } catch (InstantiationException e) {
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
            IllegalAccessException, InvocationTargetException,
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

        Object o = getter.invoke(instance);
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

    protected int getSkipBytes(Field f) throws
            IllegalAccessException, InvocationTargetException {

        SkipBytesHolder sb = skipBytesForField.get(f);
        if (sb == null) {
            try {
                sb = new SkipBytesHolder(AnnotationHelper.getAnnotation(instance.getClass(), f, SkipBytes.class));
                skipBytesForField.put(f, sb);
            } catch (AnnotationNotSpecifiedException ee) {
                skipBytesForField.put(f, noSkip);
                return 0;
            }
        }

        if (sb.getValue() != 0) {
            if (sb.getValue() == -1 ){
                return 0;
            }
            return sb.getValue();
        }

        String counterFieldName = sb.getFieldName();
        if (counterFieldName == null || counterFieldName.length() == 0){
            return 0;
        }

        Method getter;
        try {
            getter = ReflectionHelper.getGetter(instance.getClass(), counterFieldName);
        } catch (NoSuchMethodException e) {
            return 0;
        }

        Object o = getter.invoke(instance);
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

    protected boolean readList(Field f) throws
            IllegalAccessException, InvocationTargetException,
            FieldReadException {

        Type t = f.getGenericType();
        Class clazz = ((Class) ((ParameterizedType) t).getActualTypeArguments()[0]);
        long containerSize = getContainerSize(f);
        List list = null;
        try {
            Method getter = ReflectionHelper.getGetter(instance.getClass(), f.getName());
            Object obj = getter.invoke(instance);
            if (obj instanceof List) {
                list = (List) obj;
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        if (list == null) {
            list = new ArrayList();
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


        try {
            Method m = ReflectionHelper.getSetter(instance.getClass(), f.getName(), List.class);
            m.invoke(instance, list);
        } catch (NoSuchMethodException e) {
            throw new FieldReadException("Can not receive setter for " + instance.getClass().getName() +
                    " class, field " + f.getName());
        }
        return true;

    }

    protected Object readContainerEntry(Class clazz, Field f) throws FieldReadException, IllegalAccessException {
        Object o;
        if (clazz.equals(int.class) || clazz.equals(Integer.class) ||
                clazz.equals(short.class) || clazz.equals(Short.class) ||
                clazz.equals(byte.class) || clazz.equals(Byte.class) ||
                clazz.equals(long.class) || clazz.equals(Long.class)) {
            boolean bigEndian = true;
            if (f.getDeclaredAnnotation(LittleEndian.class) != null) {
                bigEndian = false;
            }
            o = readNumber(clazz, bigEndian);

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

    private static class SkipBytesHolder {

        private int value;
        private String fieldName;

        public SkipBytesHolder() {
            value = -1;
            fieldName = "";
        }

        public SkipBytesHolder(SkipBytes sb) {
            value = sb.value();
            fieldName = sb.fieldName();
        }

        public int getValue() {
            return value;
        }

        public String getFieldName() {
            return fieldName;
        }
    }
}
