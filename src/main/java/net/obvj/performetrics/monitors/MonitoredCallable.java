package net.obvj.performetrics.monitors;

import java.util.concurrent.Callable;

import net.obvj.performetrics.Counter.Type;

/**
 * <p>
 * A {@link Callable} wrapper that maintains one or more counters for monitoring the time
 * spent by the Callable's {@code call()} method.
 * </p>
 *
 * <p>
 * <b>Note:</b> This class is not thread-safe. In a multi-thread context, it is
 * recommended to create separate instances for each thread. If multiple threads access
 * the same instance of this class, the metrics may be inconsistent.
 * </p>
 *
 * @param <V> the result type of method call
 * @author oswaldo.bapvic.jr
 */
public class MonitoredCallable<V> extends MonitoredOperation implements Callable<V>
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
