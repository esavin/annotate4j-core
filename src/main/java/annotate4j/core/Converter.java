package annotate4j.core;

import annotate4j.core.exceptions.ConvertationException;
import annotate4j.core.exceptions.UnsupportedGenericClassesException;

/**
 * @author Eugene Savin
 */
public interface Converter<F, T> {

    public T convert(F from, T to) throws UnsupportedGenericClassesException, ConvertationException;
}
