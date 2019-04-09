package annotate4j.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specify postfix for field. This postfix will be written after the field when this field is dumped
 * <p/>
 *
 * @author Eugene Savin
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface FieldPostfix {

    public String value();

    /**
     * Do not process <code>FieldPostfix.value</code> if true.
     *
     * @return
     */
    public boolean skipIfFieldNull() default true;
}
