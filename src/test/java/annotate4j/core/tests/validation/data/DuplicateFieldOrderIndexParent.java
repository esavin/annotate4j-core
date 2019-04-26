package annotate4j.core.tests.validation.data;

import annotate4j.core.bin.annotation.FieldOrder;

/**
 * @author Eugene Savin
 */
public class DuplicateFieldOrderIndexParent {

    @FieldOrder(index = 1)
    private int parentField1;

    @FieldOrder(index = 2)
    private int parentField2;

    public int getParentField1() {
        return parentField1;
    }

    public void setParentField1(int parentField1) {
        this.parentField1 = parentField1;
    }

    public int getParentField2() {
        return parentField2;
    }

    public void setParentField2(int parentField2) {
        this.parentField2 = parentField2;
    }
}
