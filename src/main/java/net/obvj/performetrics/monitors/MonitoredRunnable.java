package net.obvj.performetrics.monitors;

import net.obvj.performetrics.Counter;
import net.obvj.performetrics.Counter.Type;

/**
 * <p>
 * A {@link Runnable} wrapper that maintains one or more counters for monitoring the time
 * spent by the Runnable's {@code run()} method.
 * </p>
 *
 * <p>
 * Specify a target {@code Runnable} via constructor, then execute the {@code run()}
 * method available in this wrapper. The target {@code Runnable}'s {@code run()} method
 * will be executed and monitored. After the operation, call
 * {@code printStatistics(System.out)} to print the statistics to the system console or
 * {@code getCounter(Counter.Type)}, then {@code elapsedTime()} to retrieve the elapsed
 * time for a particular counter. E.g.:
 * </p>
 *
 * <pre>
 * Counter cpuTime = monitoredRunnable.getCounter(Counter.Type.CPU_TIME);
 * double elapsedTimeMillis = cpuTime.elapsedTime(TimeUnit.MILLISECONDS);
 * </pre>
 *
 * <p>
 * By default, all available counter types will be measured, if no specific counter types
 * are passed to the constructor. If required, an additional constructor may be used to
 * set up one or more specific counters to be maintained. E.g.:
 * </p>
 *
 * <pre>
 * new MonitoredRunnable(runnable); // maintains all available counter types
 * new MonitoredRunnable(runnable, Counter.Type.WALL_CLOCK_TIME); // wall-clock time only
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
 * @author oswaldo.bapvic.jr
 * @see Counter
 * @see Counter.Type
 */
public class MonitoredRunnable extends MonitoredOperation implements Runnable
{
    private Runnable targetRunnable;

    /**
     * Builds this monitored operation with a given {@link Runnable}. All available counter
     * types will be maintained.
     *
     * @param targetRunnable the {@link Runnable} to be executed and profiled
     */
    public MonitoredRunnable(Runnable targetRunnable)
    {
        this(targetRunnable, NO_SPECIFIC_TYPE);
    }

    /**
     * Builds this monitored operation with a given {@link Runnable} and one or more specific
     * counter types to be maintained.
     *
     * @param targetRunnable the {@link Runnable} to be executed and profiled
     * @param types          the counter types to be maintained with the operation
     */
    public MonitoredRunnable(Runnable targetRunnable, Type... types)
    {
        super(types);
        this.targetRunnable = targetRunnable;
    }

    /**
     * See {@link Runnable#run()}.
     */
    @Override
    public void run()
    {
        stopwatch.reset();
        stopwatch.start();
        try
        {
            targetRunnable.run();
        }
        finally
        {
            stopwatch.stop();
        }
    }

}
