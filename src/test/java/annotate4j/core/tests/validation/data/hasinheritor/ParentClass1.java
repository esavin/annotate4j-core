package annotate4j.core.tests.validation.data.hasinheritor;

import annotate4j.core.annotation.FieldOrder;
import annotate4j.core.exceptions.InheritorNotFoundException;
import annotate4j.core.tests.utils.CauseException;
import annotate4j.core.validation.exceptions.InheritanceException;
import annotate4j.core.HasInheritor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Eugene Savin
 */
@CauseException(exception = InheritanceException.class)
public class ParentClass1 implements HasInheritor<ParentClass2> {

    @FieldOrder(index = 1)
    private int filed1;

    public int getFiled1() {
        return filed1;
    }

    public void setFiled1(int filed1) {
        this.filed1 = filed1;
    }

    @Override
    public Class<? extends ParentClass2> getInheritor() throws InheritorNotFoundException {
        return ChildClass2.class;
    }

    @Override
    public Collection<Class<? extends ParentClass2>> getInheritors() {
        List<Class<? extends ParentClass2>> l = new ArrayList<Class<? extends ParentClass2>>();
        l.add(ChildClass2.class);
        return l;
    }
}
