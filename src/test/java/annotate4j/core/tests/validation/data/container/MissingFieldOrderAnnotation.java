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
public class MissingFieldOrderAnnotation {


    private int field1;

    @ContainerSize(fieldName = "field1")
    @FieldOrder(index = 2)
    private List<MissingFieldOrderAnnotation> list;


    public int getField1() {
        return field1;
    }

    public void setField1(int field1) {
        this.field1 = field1;
    }

    public List<MissingFieldOrderAnnotation> getList() {
        return list;
    }

    public void setList(List<MissingFieldOrderAnnotation> list) {
        this.list = list;
    }
}
