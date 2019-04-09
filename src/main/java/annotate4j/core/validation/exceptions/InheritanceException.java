package annotate4j.core.validation.exceptions;

/**
 * @author Eugene Savin
 */
public class InheritanceException extends ValidationException {

    private Class inheritor;
    private Class parent;

    public InheritanceException(Class inheritor, Class parent) {
        this.inheritor = inheritor;
        this.parent = parent;
    }

    public Class getInheritor() {
        return inheritor;
    }

    public Class getParent() {
        return parent;
    }

    @Override
    public String getMessage() {
        return "The class " + inheritor.getName() + " should extends " + parent.getName();
    }
}
