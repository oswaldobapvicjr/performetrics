package net.obvj.performetrics.runnable;

import net.obvj.performetrics.Counter.Type;
import net.obvj.performetrics.MultiCounterMonitorableOperation;

/**
 * A thread-safe {@link Runnable} that maintains custom counters for elapsed time
 * evaluation.
 * <p>
 * The counters are stored in default time unit (nanoseconds).
 *
 * @author oswaldo.bapvic.jr
 */
public class MultiCounterRunnableOperation extends MultiCounterMonitorableOperation implements Runnable
{
    private Runnable targetRunnable;
    private Object lock = new Object();

    /**
     * @param targetRunnable the Runnable to be executed
     * @param types the counter types to be maintained with the operation
     */
    public MultiCounterRunnableOperation(Runnable targetRunnable, Type... types)
    {
        super(types);
        this.targetRunnable = targetRunnable;
    }

    @Override
    public void run()
    {
        synchronized (lock)
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

}
