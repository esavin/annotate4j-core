package annotate4j.core.tests.validation.data;

import annotate4j.core.bin.annotation.FieldOrder;
import annotate4j.core.tests.utils.CauseException;
import annotate4j.core.validation.exceptions.UnsupportedTypeException;

/**
 * @author Eugene Savin
 */

@CauseException(exception = UnsupportedTypeException.class)
public class UnsupportedFloat {

    @FieldOrder(index = 1)
    private float floatField;

    public float getFloatField() {
        return floatField;
    }

    public void setFloatField(float floatField) {
        this.floatField = floatField;
    }
}
