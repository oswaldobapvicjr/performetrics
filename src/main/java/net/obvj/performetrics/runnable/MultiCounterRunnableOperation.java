package net.obvj.performetrics.runnable;

import net.obvj.performetrics.Counter.Type;
import net.obvj.performetrics.MultiCounterMonitorableOperation;

/**
 * A thread-safe {@link Runnable} that maintains custom counters for elapsed time
 * evaluation.
 * <p>
 * The counters are stored in default time unit (nanoseconds).
 * <p>
 * <b>Note:</b> Not thread-safe. It is recommended to create separate instances for each
 * thread. If multiple threads access the same instance of this class, the metrics may be
 * inconsistent.
 * 
 * @author oswaldo.bapvic.jr
 */
public class MultiCounterRunnableOperation extends MultiCounterMonitorableOperation implements Runnable
{
    private Runnable targetRunnable;

    /**
     * Builds this monitorable operation with a given {@link Runnable}.
     * 
     * @param targetRunnable the {@link Runnable} to be executed and profiled
     */
    public MultiCounterRunnableOperation(Runnable targetRunnable)
    {
        this(targetRunnable, NO_SPECIFIC_TYPE);
    }
    
    /**
     * Builds this monitorable operation with a given {@link Runnable} and specific counter type(s).
     * 
     * @param targetRunnable the {@link Runnable} to be executed and profiled
     * @param types          the counter types to be maintained with the operation
     */
    public MultiCounterRunnableOperation(Runnable targetRunnable, Type... types)
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
