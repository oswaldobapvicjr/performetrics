package net.obvj.performetrics.callable;

import java.util.concurrent.Callable;

import net.obvj.performetrics.SimpleMonitorableOperation;
import net.obvj.performetrics.UnitType;
import net.obvj.performetrics.util.PerformetricsUtils;

/**
 * @author oswaldo.bapvic.jr
 */
public abstract class CpuTimeCallableOperation<V> extends SimpleMonitorableOperation implements Callable<V>
{

    private Object lock = new Object();

    public CpuTimeCallableOperation()
    {
        super(UnitType.CPU_TIME);
    }

    public V start() throws Exception
    {
        synchronized (lock)
        {
            unitsAfter = 0;
            unitsBefore = PerformetricsUtils.getCpuTimeNanos();
            try
            {
                return call();
            }
            finally
            {
                unitsAfter = PerformetricsUtils.getCpuTimeNanos();
            }
        }
    }

}
