package annotate4j.core.bin.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The annotation to skip data in file
 *
 * @author Eugene Savin
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface SkipBytes {

    /**
     * The name of field contains number of bytes to skip. This field should be integer.
     *
     * @return field name
     */
    String fieldName() default "";

    int value() default 0;

}
