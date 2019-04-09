package annotate4j.core;

import annotate4j.core.exceptions.InheritorNotFoundException;

import java.util.Collection;

/**
 * @author Eugene Savin
 */
public interface HasInheritor<T> {

    public Class<? extends T> getInheritor() throws InheritorNotFoundException;

    public Collection<Class<? extends T>> getInheritors();

}
