package net.obvj.performetrics.runnable;

import net.obvj.performetrics.Counter.Type;
import net.obvj.performetrics.SimpleMonitorableOperation;
import net.obvj.performetrics.util.PerformetricsUtils;

/**
 * @author oswaldo.bapvic.jr
 */
public abstract class CpuTimeRunnableOperation extends SimpleMonitorableOperation implements Runnable
{

    private Object lock = new Object();

    public CpuTimeRunnableOperation()
    {
        super(Type.CPU_TIME);
    }

    public void start()
    {
        synchronized (lock)
        {
            getCounter().setUnitsAfter(0);
            getCounter()
                    .setUnitsBefore(getCounter().getTimeUnit().fromNanoseconds(PerformetricsUtils.getCpuTimeNanos()));
            try
            {
                run();
            }
            finally
            {
                getCounter().setUnitsAfter(
                        getCounter().getTimeUnit().fromNanoseconds(PerformetricsUtils.getCpuTimeNanos()));
            }
        }
    }

}
