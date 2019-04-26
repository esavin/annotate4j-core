package annotate4j.core.bin.dump;

import annotate4j.core.DumpBuilder;
import annotate4j.core.annotation.FieldPostfix;
import annotate4j.core.annotation.FieldPrefix;
import annotate4j.core.bin.annotation.LittleEndian;
import annotate4j.core.bin.dump.exceptions.FieldWriteException;
import annotate4j.core.bin.utils.AnnotationHelper;
import annotate4j.core.bin.utils.FieldsBuilder;
import annotate4j.core.bin.utils.ReflectionHelper;

import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;

/**
 * @author Eugene Savin
 */
public class OutputStreamBuilder implements DumpBuilder, Cloneable {
    private DataOutput out;
    private Object instance;

    public OutputStreamBuilder(OutputStream out) {
        this.out = new DataOutputStream(out);
    }

    protected OutputStreamBuilder(DataOutput out){
        this.out = out;
    }

    public void dump(Object obj) throws FieldWriteException {
        this.instance = obj;
        if (instance == null) {
            return;
        }
        try {
            if (writeNumber()) {
                return;
            }
        } catch (IOException e) {
            new FieldWriteException("Can not write variable of class " + instance.getClass().getName(), e);
        }
        Field[] fields = FieldsBuilder.buildFields(instance, new Object());
        for (int i = 0; i < fields.length; i++) {
            Field f = fields[i];
            try {
                writeField(f);
            } catch (NoSuchMethodException e) {
                new FieldWriteException("Can not found getter for " + f.getName() + " in class " + instance.getClass().getName());
            } catch (IllegalAccessException e) {
                new FieldWriteException("Wrong access flag for getter of " + f.getName() + " field in class " + instance.getClass().getName());
            } catch (InvocationTargetException e) {
                new FieldWriteException(e);
            } catch (IOException e) {
                new FieldWriteException("Can not write field " + f.getName() + " of class " + instance.getClass().getName(), e);
            }
        }
    }

    private boolean writeNumber() throws IOException {

        if (!(instance instanceof Number)) {
            return false;
        }

        if (instance instanceof java.lang.Long) {
            writeLong();
            return true;
        }

        if (instance instanceof java.lang.Integer) {
            writeInt();
            return true;
        }
        if (instance instanceof java.lang.Short) {
            writeShort();
            return true;
        }

        if (instance instanceof java.lang.Byte) {
            writeByte();
            return true;
        }
        return false;
    }

    private void writeField(Field f) throws NoSuchMethodException,
            IllegalAccessException, InvocationTargetException, IOException, FieldWriteException {
        boolean b;

        b = writeNumber(f);

        if (!b) {
            b = writeContainer(f);
        }
        if (!b) {
            b = writeString(f);
        }

        if (!b) {
            b = writeClassInstance(f);
        }
        if (!b) {
            throw new FieldWriteException("Can not write field " + f.getName() + ", type: " + f.getType().getName() + " of class " + instance.getClass().getName());

        }
    }

    private boolean writeNumber(Field f) throws NoSuchMethodException,
            IllegalAccessException, InvocationTargetException, IOException {
        Class fieldType = f.getType();
        boolean bigEndian = true;
        if (f.getDeclaredAnnotation(LittleEndian.class) != null) {
            bigEndian = false;
        }
        if (fieldType.equals(int.class) || fieldType.equals(Integer.class)) {
            writeInt(f, bigEndian);
            return true;
        }
        if (fieldType.equals(short.class) || fieldType.equals(Short.class)) {
            writeShort(f, bigEndian);
            return true;
        }

        if (fieldType.equals(byte.class) || fieldType.equals(Byte.class)) {
            writeByte(f);
            return true;
        }
        if (fieldType.equals(long.class) || fieldType.equals(Long.class)) {
            writeLong(f, bigEndian);
            return true;
        }
        return false;
    }

    private void writeByte() throws IOException {
        Byte b = (Byte) instance;
        out.writeByte(b.intValue());


    }

    private void writeShort() throws IOException {
        Short b = (Short) instance;
        out.writeShort(b.intValue());


    }

    private void writeLong() throws IOException {
        Long l = (Long) instance;
        out.writeLong(l.longValue());
    }

    private void writeInt() throws IOException {
        Integer i = (Integer) instance;
        out.writeInt(i.intValue());
    }

    private void writeByte(Field f) throws NoSuchMethodException,
            IllegalAccessException, InvocationTargetException, IOException {
        Method m = ReflectionHelper.getGetter(instance.getClass(), f);
        Byte b = (Byte) m.invoke(instance);
        out.writeByte(b.intValue());


    }

    private void writeShort(Field f, boolean bigEndian) throws NoSuchMethodException,
            IllegalAccessException, InvocationTargetException, IOException {
        Method m = ReflectionHelper.getGetter(instance.getClass(), f);
        Short b = (Short) m.invoke(instance);
        if (!bigEndian) {
            b = Short.reverseBytes(b);
        }
        out.writeShort(b.intValue());
    }

    private void writeLong(Field f, boolean bigEndian) throws NoSuchMethodException,
            IllegalAccessException, InvocationTargetException, IOException {
        Method m = ReflectionHelper.getGetter(instance.getClass(), f);
        Long l = (Long) m.invoke(instance);
        if (!bigEndian) {
            l = Long.reverseBytes(l);
        }
        out.writeLong(l);
    }

    private void writeInt(Field f, boolean bigEndian) throws NoSuchMethodException,
            IllegalAccessException, InvocationTargetException, IOException {
        Method m = ReflectionHelper.getGetter(instance.getClass(), f);
        Integer i = (Integer) m.invoke(instance);
        if (!bigEndian) {
            i = Integer.reverseBytes(i);
        }
        out.writeInt(i.intValue());
    }

    private boolean writeString(Field f) throws NoSuchMethodException,
            IllegalAccessException, InvocationTargetException, IOException, FieldWriteException {
        Class fieldType = f.getType();
        if (fieldType.equals(String.class)) {
            Method m = ReflectionHelper.getGetter(instance.getClass(), f);
            String s = (String) m.invoke(instance);
            writePrefix(f);
            out.writeBytes(s);
            writePostfix(f);
            return true;
        }
        return false;
    }

    private void writePrefix(Field f) throws IOException {
        FieldPrefix prefix = AnnotationHelper.getAnnotationOrNull(instance.getClass(), f, FieldPrefix.class);
        if (prefix != null) {
            out.writeBytes(prefix.value());
        }
    }

    private void writePostfix(Field f) throws IOException {
        FieldPostfix prefix = AnnotationHelper.getAnnotationOrNull(instance.getClass(), f, FieldPostfix.class);
        if (prefix != null) {
            out.writeBytes(prefix.value());
        }
    }

    private boolean writeContainer(Field f) throws NoSuchMethodException,
            IllegalAccessException, InvocationTargetException, IOException, FieldWriteException {
        Class fieldType = f.getType();
        if (fieldType.equals(List.class)) {
            return writeList(f);
        } else if (fieldType.isArray()) {
            return writeArray(f, fieldType.getComponentType());
        }
        return false;
    }

    private boolean writeList(Field f) throws NoSuchMethodException,
            IllegalAccessException, InvocationTargetException, IOException, FieldWriteException {
        Method m = ReflectionHelper.getGetter(instance.getClass(), f);
        List l = (List) m.invoke(instance);
        if (l == null) return true;
        Iterator iter = l.iterator();
        while (iter.hasNext()) {
            Object obj = iter.next();
            if (obj == null) {
                continue;
            }
            if (obj instanceof String) {
                writePrefix(f);
                out.writeBytes((String) obj);
                writePostfix(f);
                continue;
            }
            DumpBuilder builder;
            try {
                builder = (DumpBuilder) clone();
            } catch (CloneNotSupportedException e) {
                throw new FieldWriteException("Can not call clone() method");
            }
            writePrefix(f);
            builder.dump(obj);
            writePostfix(f);
        }
        return true;
    }

    private boolean writeArray(Field f, Class type) throws NoSuchMethodException,
            IllegalAccessException, InvocationTargetException, IOException, FieldWriteException {


        if (type.equals(byte.class)) {
            return writeByteArray(f);
        }
        if (type.equals(int.class)) {
            return writeIntArray(f);
        }
        if (type.equals(short.class)) {
            return writeShortArray(f);
        }
        if (type.equals(String.class)) {
            return writeStringArray(f);
        }
        return false;
    }

    private boolean writeStringArray(Field f) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, IOException {
        Method m = ReflectionHelper.getGetter(instance.getClass(), f);
        String[] array = (String[]) m.invoke(instance);
        for (int i = 0; i < array.length; i++) {
            String s = array[i];
            writePrefix(f);
            out.writeBytes(s);
            writePostfix(f);
        }
        return true;
    }

    private boolean writeByteArray(Field f) throws NoSuchMethodException,
            IllegalAccessException, InvocationTargetException, IOException {

        Method m = ReflectionHelper.getGetter(instance.getClass(), f);
        byte[] array = (byte[]) m.invoke(instance);
        for (int i = 0; i < array.length; i++) {
            byte b = array[i];
            out.writeByte(b);
        }
        return true;
    }

    private boolean writeShortArray(Field f) throws NoSuchMethodException,
            IllegalAccessException, InvocationTargetException, IOException {

        Method m = ReflectionHelper.getGetter(instance.getClass(), f);
        short[] array = (short[]) m.invoke(instance);
        for (int i = 0; i < array.length; i++) {
            short b = array[i];
            out.writeShort(b);
        }
        return true;
    }

    private boolean writeIntArray(Field f) throws NoSuchMethodException,
            IllegalAccessException, InvocationTargetException, IOException {

        Method m = ReflectionHelper.getGetter(instance.getClass(), f);
        int[] array = (int[]) m.invoke(instance);
        for (int i = 0; i < array.length; i++) {
            int b = array[i];
            out.writeInt(b);
        }
        return true;
    }


    private boolean writeClassInstance(Field f) throws NoSuchMethodException,
            IllegalAccessException, InvocationTargetException, IOException, FieldWriteException {
        Method m = ReflectionHelper.getGetter(instance.getClass(), f);
        Object obj = m.invoke(instance);
        DumpBuilder builder;
        try {
            builder = (DumpBuilder) clone();
        } catch (CloneNotSupportedException e) {
            throw new FieldWriteException("Can not call clone() method");
        }
        writePrefix(f);
        builder.dump(obj);
        writePostfix(f);
        return true;
    }


    @Override
    protected Object clone() throws CloneNotSupportedException {
        return new OutputStreamBuilder(out);
    }
}
