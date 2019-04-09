package annotate4j.core.worker;

/**
 * @author Eugene Savin
 */
public interface Worker<E, T> {

    T doWork(E e);
}
