package annotate4j.core.tests.validation.data.container;

import annotate4j.core.annotation.FieldOrder;
import annotate4j.core.bin.annotation.ContainerSize;
import annotate4j.core.tests.utils.CauseException;
import annotate4j.core.validation.exceptions.AmbiguousContainerSizeValueException;

import java.util.List;

/**
 * @author Eugene Savin
 */

@CauseException(exception = AmbiguousContainerSizeValueException.class)
public class AmbiguousContainerSize {
    @FieldOrder(index = 1)
    private int field1;

    @ContainerSize(value = 1, fieldName = "field1")
    @FieldOrder(index = 2)
    private List<AmbiguousContainerSize> list;


    public int getField1() {
        return field1;
    }

    public void setField1(int field1) {
        this.field1 = field1;
    }

    public List<AmbiguousContainerSize> getList() {
        return list;
    }

    public void setList(List<AmbiguousContainerSize> list) {
        this.list = list;
    }
}
