package annotate4j.core.bin.utils;

import annotate4j.core.HasInheritor;
import annotate4j.core.bin.exceptions.ResolveException;

import java.util.Map;

/**
 * Provides mechanism to switch class <code>T</code> to inheritor <code>V</code>
 * This mechanism will be performed if <code>T</code> implements {@link HasInheritor}
 * interface
 *
 * @author Eugene Savin
 */
public interface ClassSwitcher<T, V extends T> {
    public V switchClass(T object, Map<String, Object> injectedVariable) throws ResolveException;
}
