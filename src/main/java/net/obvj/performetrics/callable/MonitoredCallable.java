package net.obvj.performetrics.callable;

import java.util.concurrent.Callable;

import net.obvj.performetrics.Counter.Type;
import net.obvj.performetrics.MultiCounterMonitorableOperation;

/**
 * A {@link Callable} that maintains one or more custom counters for elapsed time
 * evaluation.
 * <p>
 * The counters are stored in the default time unit (nanoseconds).
 * <p>
 * <b>Note:</b> Not thread-safe. In a multi-thread context, it is recommended to create
 * separate instances for each thread. If multiple threads access the same instance of
 * this class, the metrics may be inconsistent.
 *
 * @param <V> the result type of method call
 * @author oswaldo.bapvic.jr
 */
public class MonitoredCallable<V> extends MultiCounterMonitorableOperation implements Callable<V>
{
    private Callable<V> targetCallable;

    /**
     * Builds this monitored operation with a given {@link Callable}. All available counter
     * types will be maintained.
     *
     * @param targetCallable the Callable to be executed
     */
    public MonitoredCallable(Callable<V> targetCallable)
    {
        this(targetCallable, NO_SPECIFIC_TYPE);
    }

    /**
     * Builds this monitored operation with a given {@link Callable} and one or more specific
     * counter types to be maintained.
     *
     * @param targetCallable the Callable to be executed
     * @param types          the counter types to be maintained with the operation
     */
    public MonitoredCallable(Callable<V> targetCallable, Type... types)
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
