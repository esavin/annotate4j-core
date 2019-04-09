package annotate4j.core.worker;

/**
 * @author Eugene Savin
 * @version Nov 1, 2010
 */
public interface ToStringWorker<E> extends Worker<E, String>{
    String doWork(E e);
}
