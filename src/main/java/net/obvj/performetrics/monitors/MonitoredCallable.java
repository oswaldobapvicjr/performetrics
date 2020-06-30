package net.obvj.performetrics.monitors;

import java.util.Objects;
import java.util.concurrent.Callable;

import net.obvj.performetrics.Counter;
import net.obvj.performetrics.Counter.Type;

/**
 * <p>
 * A {@link Callable} wrapper that maintains one or more counters for monitoring the time
 * spent by the Callable's {@code call()} method.
 * </p>
 *
 * <p>
 * Specify a target {@code Callable} via constructor, then execute the {@code call()}
 * method available in this wrapper. The target {@code Callable}'s {@code call()} method
 * will be executed and monitored. After the operation, call
 * {@code printStatistics(System.out)} to print the statistics to the system console or
 * {@code elapsedTime(Counter.Type)}, to retrieve the elapsed time duration for a
 * particular counter. E.g.:
 * </p>
 *
 * <pre>
 * double cpuTimeNanos = monitoredRunnable.elapsedTime(Counter.Type.CPU_TIME, TimeUnit.NANOSECONDS);
 * </pre>
 *
 * <p>
 * By default, all available counter types will be measured, if no specific counter types
 * are passed to the constructor. If required, an additional constructor may be used to
 * set up one or more specific counters to be maintained. E.g.:
 * </p>
 *
 * <pre>
 * new MonitoredCallable(callable); // maintains all available counter types
 * new MonitoredCallable(callable, Counter.Type.WALL_CLOCK_TIME); // wall-clock time only
 * </pre>
 *
 * <p>
 * For a list of available counters, refer to {@link Counter.Type}.
 * </p>
 *
 * <p>
 * <b>Note:</b> This class is not thread-safe. In a multi-thread context, different
 * instances must be created for each thread.
 * </p>
 *
 * @param <V> the result type of method call
 * @author oswaldo.bapvic.jr
 * @see Counter
 * @see Counter.Type
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
        Objects.requireNonNull(targetCallable, "the target callable must not be null");
        stopwatch.reset();
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
