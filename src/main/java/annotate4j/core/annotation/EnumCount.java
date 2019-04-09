package annotate4j.core.annotation;

/**
 * Define type of nonterminal occurrences
 *
 * @author Eugene Savin
 */
public enum EnumCount {
    /**
     * Means "*" (asterisk) in grammar terms
     */
    ZERO_OR_MORE,

    /**
     * Means "+" in grammar terms
     */
    ONE_OR_MORE,

    /**
     * Means optional in grammar terms
     * <p/>
     * If optional nonterminal has zero occurrences that all <code>@Prefix</code> and <code>@Postfix</code> values have
     * also zero occurrences
     */
    ZERO_OR_ONE
}
