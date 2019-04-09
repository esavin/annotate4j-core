package annotate4j.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specify prefix for field. This prefix will be written before the field when this field is dumped
 * <p/>
 *
 * @author Eugene Savin
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface FieldPrefix {

    public String value();

    /**
     * Do not process <code>FieldPrefix.value</code> if true.
     *
     * @return
     */
    public boolean skipIfFieldNull() default true;
}
