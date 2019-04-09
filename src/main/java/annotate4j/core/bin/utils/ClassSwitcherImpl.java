package annotate4j.core.bin.utils;

import annotate4j.core.HasInheritor;
import annotate4j.core.bin.exceptions.ResolveException;
import annotate4j.core.bin.exceptions.ResolvedClassNotFoundException;
import annotate4j.core.exceptions.InheritorNotFoundException;
import annotate4j.core.bin.loader.InputStreamLoader;

import java.util.*;

/**
 * @author Eugene Savin
 */
public class ClassSwitcherImpl<T, V extends T> implements ClassSwitcher<T, V> {
    private Map<String, Object> injectedVariable = null;
    private List<ExternalClassProvider> externalClassProviders = new ArrayList<ExternalClassProvider>();

    public V switchClass(T object, Map<String, Object> injectedVariable) throws ResolveException {
        this.injectedVariable = injectedVariable;
        return switchClass(object);
    }

    protected V switchClass(T object) throws ResolveException {
        if (!ReflectionHelper.isDirectImplements(object.getClass(), HasInheritor.class)) {
            return (V) object;
        }
        HasInheritor hi = (HasInheritor) object;
        if (injectedVariable != null) {
            InputStreamLoader.injectVariable(hi, injectedVariable);
        }
        Class<V> clazz = null;
        String key = "";
        try {
            clazz = hi.getInheritor();
        } catch (InheritorNotFoundException infe) {
            key = infe.getKey();
            if (externalClassProviders != null) {
                for (ExternalClassProvider ecp : externalClassProviders) {
                    clazz = ecp.getExternalClass(object, key);
                    if (clazz != null) {
                        break;
                    }
                }
            }
        }


        if (clazz == null) {
            throw new ResolvedClassNotFoundException(object.getClass().getName(), key);
        }
        try {
            V instance = clazz.newInstance();
            if (injectedVariable != null) {
                InputStreamLoader.injectVariable(instance, injectedVariable);
            }
            return instance;
        } catch (InstantiationException e1) {
            throw new ResolveException(e1);
        } catch (IllegalAccessException e2) {
            throw new ResolveException(e2);
        }
    }




    public void setExternalClassProviders(List<ExternalClassProvider> externalClassProviders) {
        this.externalClassProviders = externalClassProviders;
    }
}
