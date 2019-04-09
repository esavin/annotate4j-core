package annotate4j.core;

import annotate4j.core.bin.dump.exceptions.FieldWriteException;

/**
 * @author Eugene Savin
 */
public interface DumpBuilder {
    void dump(Object obj) throws FieldWriteException;
}
