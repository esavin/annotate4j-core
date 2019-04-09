package annotate4j.core.tests.validation.data.hasinheritor;

import annotate4j.core.annotation.FieldOrder;

/**
 * @author Eugene Savin
 */
public class InheritorClass extends PrivateConstructor {

    public InheritorClass(int field) {
        super(field);
    }

    @FieldOrder(index = 2)
    private int field2;

    public int getField2() {
        return field2;
    }

    public void setField2(int field2) {
        this.field2 = field2;
    }
}
