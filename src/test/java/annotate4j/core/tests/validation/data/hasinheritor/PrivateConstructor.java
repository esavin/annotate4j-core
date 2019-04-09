package annotate4j.core.tests.validation.data.hasinheritor;

import annotate4j.core.annotation.FieldOrder;
import annotate4j.core.exceptions.InheritorNotFoundException;
import annotate4j.core.tests.utils.CauseException;
import annotate4j.core.validation.exceptions.ValidationIllegalAccessException;
import annotate4j.core.HasInheritor;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Eugene Savin
 */
@CauseException(exception = ValidationIllegalAccessException.class)
public class PrivateConstructor implements HasInheritor<PrivateConstructor> {

    private PrivateConstructor() {
    }

    public PrivateConstructor(int field) {
        this.field1 = field;
    }

    @FieldOrder(index = 1)
    private int field1;

    public int getField1() {
        return field1;
    }

    public void setField1(int field1) {
        this.field1 = field1;
    }

    @Override
    public Class<? extends PrivateConstructor> getInheritor() throws InheritorNotFoundException {
        return InheritorClass.class;
    }

    @Override
    public Collection<Class<? extends PrivateConstructor>> getInheritors() {
        ArrayList<Class<? extends PrivateConstructor>> l = new ArrayList<Class<? extends PrivateConstructor>>();
        l.add(InheritorClass.class);
        return l;
    }
}
