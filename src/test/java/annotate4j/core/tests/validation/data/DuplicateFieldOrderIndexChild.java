package annotate4j.core.tests.validation.data;

import annotate4j.core.annotation.FieldOrder;
import annotate4j.core.tests.utils.CauseException;
import annotate4j.core.validation.exceptions.DuplicateFieldOrderIndexException;

/**
 * @author Eugene Savin
 */

@CauseException(exception = DuplicateFieldOrderIndexException.class)
public class DuplicateFieldOrderIndexChild extends DuplicateFieldOrderIndexParent {

    @FieldOrder(index = 2)
    private int childField1;

    @FieldOrder(index = 3)
    private int childField2;

    public int getChildField1() {
        return childField1;
    }

    public void setChildField1(int childField1) {
        this.childField1 = childField1;
    }

    public int getChildField2() {
        return childField2;
    }

    public void setChildField2(int childField2) {
        this.childField2 = childField2;
    }
}
