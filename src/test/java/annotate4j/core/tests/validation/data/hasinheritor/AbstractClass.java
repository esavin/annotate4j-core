package annotate4j.core.tests.validation.data.hasinheritor;

import annotate4j.core.HasInheritor;
import annotate4j.core.bin.annotation.FieldOrder;
import annotate4j.core.tests.utils.CauseException;
import annotate4j.core.validation.exceptions.ValidationInstantiationException;

/**
 * @author Eugene Savin
 */

@CauseException(exception = ValidationInstantiationException.class)
public abstract class AbstractClass implements HasInheritor<AbstractClass> {

    @FieldOrder(index = 1)
    private int field1;

    public int getField1() {
        return field1;
    }

    public void setField1(int field1) {
        this.field1 = field1;
    }
}
