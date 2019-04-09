package annotate4j.core.bin.utils;

/**
 * @author Eugene Savin
 */
public class ClassPair {

    private String className;
    private String parentClassName;

    ClassPair(String className, String parentClassName) {
        this.className = className;
        this.parentClassName = parentClassName;
    }

    public int hashCode() {
        StringBuffer sb = new StringBuffer();
        sb.append(className);
        sb.append(parentClassName);
        int hash = sb.toString().hashCode();
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof ClassPair)) return false;
        ClassPair cp = (ClassPair) obj;
        if (className.equals(cp.className) && parentClassName.equals(cp.parentClassName)) return true;
        return false;
    }
}
