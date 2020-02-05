package net.obvj.performetrics.monitors;

import net.obvj.performetrics.Counter.Type;

/**
 * <p>
 * A {@link Runnable} wrapper that maintains one or more counters for monitoring the time
 * spent by the Runnable's {@code run()} method.
 * </p>
 *
 * <p>
 * <b>Note:</b> This class is not thread-safe. In a multi-thread context, different
 * instances must be created for each thread.
 * </p>
 *
 * @author oswaldo.bapvic.jr
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
