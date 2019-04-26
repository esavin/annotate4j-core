package annotate4j.core.tests.validation.data.hasinheritor;

import annotate4j.core.bin.annotation.FieldOrder;

/**
 * @author Eugene Savin
 */
public class ParentClass2 {

    @FieldOrder(index = 1)
    private int parentClass2field1;

    public int getParentClass2field1() {
        return parentClass2field1;
    }

    public void setParentClass2field1(int parentClass2field1) {
        this.parentClass2field1 = parentClass2field1;
    }
}
