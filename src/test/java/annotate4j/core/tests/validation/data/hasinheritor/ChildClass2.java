package annotate4j.core.tests.validation.data.hasinheritor;

import annotate4j.core.bin.annotation.FieldOrder;

/**
 * @author Eugene Savin
 */
public class ChildClass2 extends ParentClass2 {

    @FieldOrder(index = 2)
    private int childClass2field1;

    public int getChildClass2field1() {
        return childClass2field1;
    }

    public void setChildClass2field1(int childClass2field1) {
        this.childClass2field1 = childClass2field1;
    }
}
