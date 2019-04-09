package annotate4j.core.exceptions;

import annotate4j.core.bin.exceptions.FieldReadException;

/**
 * @author Eugene Savin
 */
public class InheritorNotFoundException extends FieldReadException {

    private String key = null;

    public InheritorNotFoundException(String message) {
        super(message);
    }

    public InheritorNotFoundException(String className, String keyValue) {
        super("Can not found " + className + " inheritor by key " + keyValue);
        key = keyValue;
    }

    public String getKey() {
        return key;
    }
}
