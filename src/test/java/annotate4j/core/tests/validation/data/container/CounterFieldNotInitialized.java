package annotate4j.core.tests.validation.data.container;

import annotate4j.core.annotation.FieldOrder;
import annotate4j.core.bin.annotation.ContainerSize;
import annotate4j.core.tests.utils.CauseException;
import annotate4j.core.validation.exceptions.CounterFieldNotInitializedException;

import java.util.List;

/**
 * @author Eugene Savin
 */
@CauseException(exception = CounterFieldNotInitializedException.class)
public class CounterFieldNotInitialized {

    @FieldOrder(index = 2)
    private int field1;

    @ContainerSize(fieldName = "field1")
    @FieldOrder(index = 1)
    private List<CounterFieldNotInitialized> list;

    public int getField1() {
        return field1;
    }

    public void setField1(int field1) {
        this.field1 = field1;
    }

    public List<CounterFieldNotInitialized> getList() {
        return list;
    }

    public void setList(List<CounterFieldNotInitialized> list) {
        this.list = list;
    }
}
