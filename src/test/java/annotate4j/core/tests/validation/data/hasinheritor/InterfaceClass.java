package annotate4j.core.tests.validation.data.hasinheritor;

import annotate4j.core.HasInheritor;
import annotate4j.core.bin.annotation.FieldOrder;
import annotate4j.core.tests.utils.CauseException;
import annotate4j.core.validation.exceptions.UnsupportedTypeException;

/**
 * @author Eugene Savin
 */

@CauseException(exception = UnsupportedTypeException.class)
public interface InterfaceClass extends HasInheritor<InterfaceClass> {

    @FieldOrder(index = 1)
    public int field1 = 1;

    public int getField1();

    public void setField1(int field1);
}
