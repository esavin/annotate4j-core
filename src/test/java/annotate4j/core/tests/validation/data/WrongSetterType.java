package annotate4j.core.tests.validation.data;

import annotate4j.core.annotation.FieldOrder;
import annotate4j.core.tests.utils.CauseException;
import annotate4j.core.validation.exceptions.SetterNotFoundException;

/**
 * @author Eugene Savin
 */

@CauseException(exception = SetterNotFoundException.class)
public class WrongSetterType {
    @FieldOrder(index = 1)
    private int field;

    public int getField() {
        return field;
    }

    public void setField(Integer field) {
        this.field = field;
    }
}
