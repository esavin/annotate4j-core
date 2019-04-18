package annotate4j.core.bin.annotation;

import java.lang.annotation.*;

/**
 * @author Eugene Savin
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Repeatable(InjectContainer.class)
public @interface Inject {
    String fieldName();
}
