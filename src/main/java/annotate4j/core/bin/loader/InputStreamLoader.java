package annotate4j.core.bin.loader;

import annotate4j.core.annotation.LittleEndian;
import annotate4j.core.bin.annotation.Inject;
import annotate4j.core.bin.annotation.InjectContainer;
import annotate4j.core.bin.exceptions.FieldReadException;
import annotate4j.core.bin.exceptions.NotSupportedTypeException;
import annotate4j.core.bin.exceptions.ResolveException;
import annotate4j.core.bin.utils.*;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Eugene Savin
 */
public class InputStreamLoader extends GenericLoader implements Cloneable {
    private Logger log = Logger.getLogger(InputStreamLoader.class.getName());

    public InputStreamLoader(InputStream is, Object instance) {
        this.parent = new Object();
        this.in = new DataInputStream(is);
        this.instance = instance;
    }

    protected InputStreamLoader(DataInput in, long offset, long level, String offsetClassName,
                              String offsetFieldName, Map<String, Object> injectedVariable) {
        this.in = in;
        this.offset = offset;
        this.parent = new Object();
        this.level = level;
        this.offsetClassName = offsetClassName;
        this.offsetFieldName = offsetFieldName;
        this.injectedVariable = injectedVariable;
    }


    protected void readField(Field f) throws FieldReadException {
        Class fieldType = f.getType();
        Method m;
        try {
            m = ReflectionHelper.getSetter(instance.getClass(), f.getName(), f.getType());
        } catch (NoSuchMethodException e) {
            throw new FieldReadException("Can not found public setter: " + e.getMessage());
        }

        try {
            boolean b = false;
            try {
                boolean bigEndian = true;
                if (f.getDeclaredAnnotation(LittleEndian.class) != null){
                    bigEndian = false;
                }
                Number n = readNumber(fieldType, bigEndian);
                if (n != null) {
                    m.invoke(instance, n);
                    b = true;
                }
            } catch (NotSupportedTypeException e) {//skip it
            }
            if (!b) {
                b = readContainer(f);
            }
            if (!b) {
                b = readClassInstance(f);
            }
            if (!b) {
                throw new FieldReadException("Can not read " + fieldType.getName());
            }
        } catch (IOException e) {
            throw new FieldReadException("Can not read " + fieldType.getName() + " from DataInput", e);
        } catch (IllegalAccessException e) {
            throw new FieldReadException("Can not invoke method " + m.getName(), e);
        } catch (InvocationTargetException e1) {
            throw new FieldReadException("Can not invoke method " + m.getName(), e1);
        }


    }


    public Object load() throws FieldReadException {
        level++;
        if (log.isLoggable(Level.FINE)) {
            StringBuffer sb = new StringBuffer();
            for (int k = 0; k < level; k++) {
                sb.append("    ");
            }

            sb.append("load ").append(instance.getClass().getName()).
                    append(" class, parent class: ").append(parent.getClass().getName());
            log.fine(sb.toString());
            if (instance.getClass().getName().equals(offsetClassName)) {
                log.fine("there are " + offset + " bytes before " + offsetClassName);
            }

        }
        Field[] fields = FieldsBuilder.buildFields(instance, parent);

        for (int i = 0; i < fields.length; i++) {
            Field f = fields[i];
            if (log.isLoggable(Level.FINE)) {
                if (f.getName().equals(offsetFieldName)) {
                    log.fine("there are : " + offset + " bytes before " + offsetFieldName);
                }
            }

            isNeedInjection = false;
            List<Inject> injects = new ArrayList<>();

            Inject singleInject = AnnotationHelper.getAnnotationOrNull(instance.getClass(), f, Inject.class);
            InjectContainer injectContainer = AnnotationHelper.getAnnotationOrNull(instance.getClass(), f, InjectContainer.class);
            if (singleInject != null){
                injects.add(singleInject);
            }
            if (injectContainer != null){
                Inject[] injectArray = injectContainer.value();
                injects.addAll(Arrays.asList(injectArray));
            }
            for (Inject inject: injects ) {
                if (inject != null) {
                    try {
                        Object obj = null;
                        if (injectedVariable != null) {
                            obj = injectedVariable.get(inject.fieldName());
                        }
                        if (obj == null) {
                            Method m = ReflectionHelper.getGetter(instance.getClass(), inject.fieldName());
                            obj = m.invoke(instance);
                        }
                        if (obj == null) {
                            throw new FieldReadException("Can not receive field " + inject.fieldName() + " into class " + instance.getClass());
                        }
                        if (injectedVariable == null) {
                            injectedVariable = new HashMap<String, Object>();
                        }
                        injectedVariable.put(inject.fieldName(), obj);
                        isNeedInjection = true;
                    } catch (NoSuchMethodException e) {
                        throw new FieldReadException("Can not found getter for field " + inject.fieldName() + " in class " + instance.getClass().getName());
                    } catch (IllegalAccessException e1) {
                        throw new FieldReadException("Can not invoke getter for field " + inject.fieldName() + " in class " + instance.getClass().getName());
                    } catch (IllegalArgumentException e2) {
                        throw new FieldReadException("Can not invoke getter for field " + inject.fieldName() + " in class " + instance.getClass().getName());
                    } catch (InvocationTargetException e3) {
                        throw new FieldReadException("Can not invoke getter for field " + inject.fieldName() + " in class " + instance.getClass().getName());
                    }
                }
            }
            try {
                readField(f);
            } catch (FieldReadException fre){
                if (fre.getCause() instanceof EOFException){
                    if (i > 0 || !parent.getClass().equals(Object.class)) { // TODO: it seems additiona checks should be performed
                        // there for some specific cases (when this exception happens on the first field in the subclass after the switch on ClassSwitcher)
                        // do not want to implement it now
                        log.warning("partial instance created for the last record in the file");
                        fre.setPartialCreatedInstance(instance);
                    }
                }
                throw fre;
            }
            if (log.isLoggable(Level.FINE)) {
                Object value = new String();
                try {
                    Method m = ReflectionHelper.getGetter(instance.getClass(), f);
                    value = m.invoke(instance);
                } catch (Exception e) {
                    //
                }


                StringBuffer sb = new StringBuffer();
                for (int k = 0; k < level; k++) {
                    sb.append("    ");
                }
                sb.append("  ").append(f.getType().getName()).append(" ").append(f.getName()).
                        append(" = `").append(value).append("`");
                log.fine(sb.toString());

            }
        }

        if (cs == null) {
            throw new FieldReadException("Can not switch structure: classSwitcher not defined (null)");
        }

        Object inheritor;
        try {
            inheritor = cs.switchClass(instance, injectedVariable);
        } catch (ResolveException e) {
            throw new FieldReadException(e);
        }

        if (inheritor != instance) {
            GenericLoader loader;
            try {
                if (injectedVariable != null) {
                    isNeedInjection = true;
                }
                loader = (GenericLoader) this.clone();
            } catch (CloneNotSupportedException e) {
                throw new FieldReadException("Can not call clone() method");
            }
            CastHelper castHelper = new CastHelperImpl();
            castHelper.cast(instance, inheritor);
            inheritor = loader.load(inheritor, instance);
            offset = loader.getOffset();
            level--;
            return inheritor;
        }
        level--;
        return instance;
    }

    public static void injectVariable(Object injectable, Map<String, Object> injectedVariable) throws ResolveException {
        Set<String> keys = injectedVariable.keySet();
        Iterator<String> iter = keys.iterator();
        while (iter.hasNext()) {
            String key = iter.next();
            Object obj = injectedVariable.get(key);
            Method m;
            try {
                Type t = obj.getClass();
                if (((Class) t).getName().equals("java.util.ArrayList")) {
                    t = List.class;
                }
                m = ReflectionHelper.getSetter(injectable.getClass(), key, (Class) t);
                if (m != null) {
                    m.invoke(injectable, obj);
                }

            } catch (NoSuchMethodException e) {
                // skip it: the target class does not contain setter method for variable that should be injected
                // it is responsibility of the target class to implement it
                // the source of injection just declare that it ready to provide the injected value
                //throw new ResolveException("can not inject ..."); // TODO define exception
            } catch (IllegalAccessException e) {
                throw new ResolveException("can not invoke setter ...", e); // TODO define exception
            } catch (InvocationTargetException e) {
                throw new ResolveException("can not invoke setter ...", e); // TODO define exception
            }
        }
    }

    public Logger getLog() {
        return log;
    }

}
