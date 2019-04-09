package annotate4j.core.exceptions;

import annotate4j.core.Converter;

/**
 * @author Eugene Savin
 */
public class UnsupportedGenericClassesException extends Exception {

    private Converter converter;
    private String from;
    private String to;

    public UnsupportedGenericClassesException(Converter converter, String from, String to) {
        this.from = from;
        this.to = to;
        this.converter = converter;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public Converter getConverter() {
        return converter;
    }

    @Override
    public String getMessage() {
        return "The converter " + getConverter().getClass().getName() + " can not convert " + getFrom() +
                " to " + getTo();
    }
}
