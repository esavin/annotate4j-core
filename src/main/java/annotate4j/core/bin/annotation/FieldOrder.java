package annotate4j.core.bin.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declare order in witch the field stored.
 * While the implementation uses reflection API and this API does not allow to get <code>Class</code> fields in any
 * particular order.
 *
 * @author Eugene Savin
 * @see Class#getFields()
 * @see Class#getDeclaredFields()
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface FieldOrder {
    int index();
}
