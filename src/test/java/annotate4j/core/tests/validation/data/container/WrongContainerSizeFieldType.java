package annotate4j.core.tests.validation.data.container;

import annotate4j.core.annotation.FieldOrder;
import annotate4j.core.bin.annotation.ContainerSize;
import annotate4j.core.tests.utils.CauseException;
import annotate4j.core.validation.exceptions.WrongContainerSizeFieldTypeException;

import java.util.List;

/**
 * @author Eugene Savin
 */
@CauseException(exception = WrongContainerSizeFieldTypeException.class)
public class WrongContainerSizeFieldType {

    @FieldOrder(index = 1)
    private WrongContainerSizeFieldType field1;

    @ContainerSize(fieldName = "field1")
    @FieldOrder(index = 2)
    private List<WrongContainerSizeFieldType> list;


    public WrongContainerSizeFieldType getField1() {
        return field1;
    }

    public void setField1(WrongContainerSizeFieldType field1) {
        this.field1 = field1;
    }

    public List<WrongContainerSizeFieldType> getList() {
        return list;
    }

    public void setList(List<WrongContainerSizeFieldType> list) {
        this.list = list;
    }
}
