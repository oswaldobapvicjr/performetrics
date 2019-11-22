package net.obvj.performetrics.callable;

import java.util.concurrent.Callable;

import net.obvj.performetrics.Counter.Type;
import net.obvj.performetrics.MultiCounterMonitorableOperation;

/**
 * A thread-safe {@link Callable} that maintains custom counters for elapsed time
 * evaluation.
 * <p>
 * The counters are stored in default time unit (nanoseconds).
 *
 * @param <V> the result type of method <tt>call</tt>
 * @author oswaldo.bapvic.jr
 */
public class MultiCounterCallableOperation<V> extends MultiCounterMonitorableOperation implements Callable<V>
{
    private Callable<V> targetCallable;

    /**
     * Builds this monitorable operation with a given {@link Callable}.
     * 
     * @param targetCallable the Callable to be executed
     */
    public MultiCounterCallableOperation(Callable<V> targetCallable)
    {
        this(targetCallable, NO_SPECIFIC_TYPE);
    }
    
    /**
     * Builds this monitorable operation with a given {@link Callable} and specific counter type(s).
     * 
     * @param targetCallable the Callable to be executed
     * @param types          the counter types to be maintained with the operation
     */
    public MultiCounterCallableOperation(Callable<V> targetCallable, Type... types)
    {
        super(types);
        this.targetCallable = targetCallable;
    }

    /**
     * See {@link Callable#call()}.
     */
    @Override
    public V call() throws Exception
    {
        stopwatch.start();
        try
        {
            return targetCallable.call();
        }
        finally
        {
            stopwatch.stop();
        }
    }

}
