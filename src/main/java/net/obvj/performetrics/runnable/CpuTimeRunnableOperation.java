package net.obvj.performetrics.runnable;

import static java.util.concurrent.TimeUnit.NANOSECONDS;

import net.obvj.performetrics.Counter.Type;
import net.obvj.performetrics.SimpleMonitorableOperation;
import net.obvj.performetrics.util.PerformetricsUtils;

/**
 * A {@link Runnable} that maintains a CPU time counter for elapsed time evaluation.
 * <p>
 * The CPU time is stored in nanoseconds.
 * <p>
 * <b>Note:</b> Not thread-safe. It is recommended to create separate instances for each
 * thread. If multiple threads access the same instance of this class, the metrics may be
 * inconsistent.
 * 
 * @author oswaldo.bapvic.jr
 */
public class CpuTimeRunnableOperation extends SimpleMonitorableOperation implements Runnable
{
    private Runnable targetRunnable;

    /**
     * Builds this monitorable operation with a given {@link Runnable} to be profiled using
     * CPU time.
     * 
     * @param targetRunnable the {@link Runnable} to be executed and profiled
     */
    public CpuTimeRunnableOperation(Runnable targetRunnable)
    {
        super(Type.CPU_TIME, NANOSECONDS);
        this.targetRunnable = targetRunnable;
    }

    /**
     * See {@link Runnable#run()}.
     */
    @Override
    public void run()
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
