package annotate4j.core.bin.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * All containers should be annotated with <code>ContainerSize</code> annotation to determinate the size of container.
 * Otherwise it is impossible to read container: the count of elements in container will be unknown.
 * <p/>
 * The current implementation supports two types of containers: <code>java.util.List</code> and array.
 *
 * @author Eugene Savin
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface ContainerSize {

    /**
     * The name of field contains container size. This field should be integer or long type.
     *
     * @return field name
     */
    public String fieldName() default "";

    public long corrector() default 0;

    public long value() default -1;

}
