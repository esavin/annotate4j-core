package annotate4j.core.bin.exceptions;

import annotate4j.core.annotation.FieldOrder;

/**
 * @author Eugene Savin
 */
public class OrderNotSpecifiedException extends AnnotationNotSpecifiedException {

    public OrderNotSpecifiedException(String fieldName, String className) {
        super(FieldOrder.class.getName(), fieldName, className);
    }

}
