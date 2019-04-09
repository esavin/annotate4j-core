package annotate4j.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines terminal name
 * All annotated terminals can be returned by <code>Parser.getNamedElements(String name)</code> method
 *
 * @author Eugene Savin
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface Terminal {
    public String name() default "";
}
