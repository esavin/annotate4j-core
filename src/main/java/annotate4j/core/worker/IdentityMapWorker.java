package annotate4j.core.worker;

import java.util.IdentityHashMap;

/**
 * @author Eugene Savin
 */
public interface IdentityMapWorker<E> extends Worker<E, IdentityHashMap>{

    @Override
    public IdentityHashMap<Object, String> doWork(E e);
}
