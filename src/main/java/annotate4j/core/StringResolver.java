package annotate4j.core;

import annotate4j.core.bin.exceptions.ResolveException;

/**
 * @author Eugene Savin
 */
public interface StringResolver {

    public String resolve(int index) throws ResolveException;
}
