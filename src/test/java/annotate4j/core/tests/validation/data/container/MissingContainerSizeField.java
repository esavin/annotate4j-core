package annotate4j.core.tests.validation.data.container;

import annotate4j.core.annotation.FieldOrder;
import annotate4j.core.bin.annotation.ContainerSize;
import annotate4j.core.tests.utils.CauseException;
import annotate4j.core.validation.exceptions.MissingContainerSizeFieldException;

import java.util.List;

/**
 * @author Eugene Savin
 */
@CauseException(exception = MissingContainerSizeFieldException.class)
public class MissingContainerSizeField {

    @FieldOrder(index = 1)
    private int field1;

    @ContainerSize(fieldName = "field2")
    @FieldOrder(index = 2)
    private List<MissingContainerSizeField> list;


    public int getField1() {
        return field1;
    }

    public void setField1(int field1) {
        this.field1 = field1;
    }

    public List<MissingContainerSizeField> getList() {
        return list;
    }

    public void setList(List<MissingContainerSizeField> list) {
        this.list = list;
    }
}
