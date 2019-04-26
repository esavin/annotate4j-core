package annotate4j.core.tests.validation.data;

import annotate4j.core.bin.annotation.FieldOrder;
import annotate4j.core.tests.utils.CauseException;
import annotate4j.core.validation.exceptions.DuplicateFieldOrderIndexException;

/**
 * @author Eugene Savin
 */
@CauseException(exception = DuplicateFieldOrderIndexException.class)
public class DuplicateFieldOrderIndex {

    @FieldOrder(index = 1)
    private int field1;

    @FieldOrder(index = 1)
    private int field2;

    public int getField1() {
        return field1;
    }

    public void setField1(int field1) {
        this.field1 = field1;
    }

    public int getField2() {
        return field2;
    }

    public void setField2(int field2) {
        this.field2 = field2;
    }
}
