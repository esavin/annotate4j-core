package annotate4j.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Groups nonterminals by <code>suiteName</code> in class annotated with <code>GrammarSwitch</code>
 *
 * @author Eugene Savin
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface SwitchSuite {
    public String suiteName();
}
