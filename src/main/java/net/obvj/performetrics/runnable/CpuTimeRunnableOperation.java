package net.obvj.performetrics.runnable;

import net.obvj.performetrics.SimpleMonitorableOperation;
import net.obvj.performetrics.UnitType;
import net.obvj.performetrics.util.PerformetricsUtils;

/**
 * @author oswaldo.bapvic.jr
 */
public abstract class CpuTimeRunnableOperation extends SimpleMonitorableOperation implements Runnable
{

    private Object lock = new Object();

    public CpuTimeRunnableOperation()
    {
        super(UnitType.CPU_TIME);
    }

    public void start()
    {
        synchronized (lock)
        {
            unitsAfter = 0;
            unitsBefore = PerformetricsUtils.getCpuTimeNanos();
            try
            {
                run();
            }
            finally
            {
                unitsAfter = PerformetricsUtils.getCpuTimeNanos();
            }
        }
    }

}
