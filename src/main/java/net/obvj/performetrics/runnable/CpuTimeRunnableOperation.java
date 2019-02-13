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
public abstract class CpuTimeRunnableOperation extends SimpleMonitorableOperation implements Runnable
{

    private Object lock = new Object();

    public CpuTimeRunnableOperation()
    {
        super(Type.CPU_TIME, NANOSECONDS);
    }

    public void start()
    {
        synchronized (lock)
        {
            getCounter().setUnitsAfter(0);
            getCounter().setUnitsBefore(PerformetricsUtils.getCpuTimeNanos());
            try
            {
                run();
            }
            finally
            {
                getCounter().setUnitsAfter(PerformetricsUtils.getCpuTimeNanos());
            }
        }
    }

}
