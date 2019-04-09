package annotate4j.core.tests.validation.data;

import annotate4j.core.annotation.FieldOrder;
import annotate4j.core.tests.utils.CauseException;
import annotate4j.core.validation.exceptions.GetterNotFoundException;

/**
 * @author Eugene Savin
 */

@CauseException(exception = GetterNotFoundException.class)
public class ProtectedGetter {
    @FieldOrder(index = 1)
    private int field;

    protected int getField() {
        return field;
    }

    public void setField(int field) {
        this.field = field;
    }
}
