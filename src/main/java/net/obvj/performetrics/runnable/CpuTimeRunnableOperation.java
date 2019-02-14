package net.obvj.performetrics.runnable;

import static java.util.concurrent.TimeUnit.NANOSECONDS;

import net.obvj.performetrics.Counter.Type;
import net.obvj.performetrics.SimpleMonitorableOperation;
import net.obvj.performetrics.util.PerformetricsUtils;

/**
 * A {@link Runnable} that maintains a CPU time counter for elapsed time evaluation.
 * <p>
 * The CPU time is stored in nanoseconds.
 *
 * @author oswaldo.bapvic.jr
 */
public class CpuTimeRunnableOperation extends SimpleMonitorableOperation implements Runnable
{
    private Runnable targetRunnable;
    private Object lock = new Object();

    public CpuTimeRunnableOperation(Runnable targetRunnable)
    {
        super(Type.CPU_TIME, NANOSECONDS);
        this.targetRunnable = targetRunnable;
    }

    @Override
    public void run()
    {
        synchronized (lock)
        {
            counter.setUnitsAfter(0);
            counter.setUnitsBefore(PerformetricsUtils.getCpuTimeNanos());
            try
            {
                targetRunnable.run();
            }
            finally
            {
                counter.setUnitsAfter(PerformetricsUtils.getCpuTimeNanos());
            }
        }
    }

}
