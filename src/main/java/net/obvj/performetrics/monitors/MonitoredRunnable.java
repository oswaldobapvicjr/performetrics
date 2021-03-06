package net.obvj.performetrics.monitors;

import java.util.Objects;

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
 * will be executed and monitored.
 * </p>
 *
 * <p>
 * After the operation, call {@code printSummary()} or {@code printDetails()} to print the
 * elapsed times or {@code elapsedTime(Counter.Type)}, to retrieve the elapsed time
 * duration for a particular counter. E.g.:
 * </p>
 *
 * <pre>
 * Duration cpuTime = monitoredRunnable.elapsedTime(Counter.Type.CPU_TIME);
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
 *
 * @see Counter
 * @see Counter.Type
 */
public class MonitoredRunnable extends MonitoredOperation implements Runnable
{
    private Runnable runnable;

    /**
     * Builds this monitored operation with a given {@link Runnable}. All available counter
     * types will be maintained.
     *
     * @param runnable the {@link Runnable} to be executed and profiled
     */
    public MonitoredRunnable(Runnable runnable)
    {
        this(runnable, NO_SPECIFIC_TYPE);
    }

    /**
     * Builds this monitored operation with a given {@link Runnable} and one or more specific
     * counter types to be maintained.
     *
     * @param runnable the {@link Runnable} to be executed and profiled
     * @param types    the counter types to be maintained with the operation
     */
    public MonitoredRunnable(Runnable runnable, Type... types)
    {
        super(types);
        this.runnable = runnable;
    }

    @Override
    public void run()
    {
        Objects.requireNonNull(runnable, "The target Runnable must not be null");
        stopwatch.start();
        try
        {
            runnable.run();
        }
        finally
        {
            stopwatch.stop();
        }
    }

}
