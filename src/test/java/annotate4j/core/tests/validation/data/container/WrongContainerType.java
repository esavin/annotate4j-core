package annotate4j.core.tests.validation.data.container;

import annotate4j.core.bin.annotation.ContainerSize;
import annotate4j.core.bin.annotation.FieldOrder;
import annotate4j.core.tests.utils.CauseException;
import annotate4j.core.validation.exceptions.UnsupportedTypeException;

import java.util.Collection;

/**
 * @author Eugene Savin
 */

@CauseException(exception = UnsupportedTypeException.class)
public class WrongContainerType {
    @FieldOrder(index = 1)
    private int field1;

    @ContainerSize(fieldName = "field1")
    @FieldOrder(index = 2)
    private Collection<WrongContainerType> list;


    public int getField1() {
        return field1;
    }

    public void setField1(int field1) {
        this.field1 = field1;
    }

    public Collection<WrongContainerType> getList() {
        return list;
    }

    public void setList(Collection<WrongContainerType> list) {
        this.list = list;
    }
}
