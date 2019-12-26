package net.obvj.performetrics.runnable;

import net.obvj.performetrics.Counter.Type;
import net.obvj.performetrics.MonitoredOperation;

/**
 * A {@link Runnable} that maintains one or more custom counters for elapsed time
 * evaluation.
 * <p>
 * The counters are stored in the default time unit (nanoseconds).
 * <p>
 * <b>Note:</b> Not thread-safe. In a multi-thread context, it is recommended to create
 * separate instances for each thread. If multiple threads access the same instance of
 * this class, the metrics may be inconsistent.
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
