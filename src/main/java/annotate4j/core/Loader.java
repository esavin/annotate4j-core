package annotate4j.core;

import annotate4j.core.bin.exceptions.FieldReadException;


/**
 * @author Eugene Savin
 */
public interface Loader {
    Object load() throws FieldReadException;
}
