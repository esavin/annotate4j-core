package annotate4j.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Implement OR logic in grammar terms.
 * <p/>
 * It allows to define following construction:
 * <p/>
 * NONTERMINAL => NT1 | NT2 | NT3, etc.
 * <p/>
 * If this annotations present then one of provided fields will be not null after parsing. Others should be null.
 * <p/>
 * If <code>SwitchSuite</code> annotations present, than fields (or methods) with same <code>SwitchSuite.suiteName</code>
 * processed as same nonterminal
 *
 * @author Eugene Savin
 * @see SwitchSuite
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface GrammarSwitch {

}
