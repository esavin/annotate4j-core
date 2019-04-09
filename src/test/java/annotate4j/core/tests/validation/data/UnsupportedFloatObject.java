package annotate4j.core.tests.validation.data;

import annotate4j.core.annotation.FieldOrder;
import annotate4j.core.tests.utils.CauseException;
import annotate4j.core.validation.exceptions.UnsupportedTypeException;

/**
 * @author Eugene Savin
 */

@CauseException(exception = UnsupportedTypeException.class)
public class UnsupportedFloatObject {

    @FieldOrder(index = 1)
    private Float floatObject;

    public Float getFloatObject() {
        return floatObject;
    }

    public void setFloatObject(Float floatObject) {
        this.floatObject = floatObject;
    }
}
