package annotate4j.core.bin.utils;

/**
 * @author Eugene Savin
 */
public interface ExternalClassProvider {

    public Class getExternalClass(Object switchedStructure, String key);
}
