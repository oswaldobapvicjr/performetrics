package net.obvj.performetrics.callable;

import java.util.concurrent.Callable;

import net.obvj.performetrics.Counter.Type;
import net.obvj.performetrics.SimpleMonitorableOperation;
import net.obvj.performetrics.util.PerformetricsUtils;

/**
 * @author oswaldo.bapvic.jr
 */
public abstract class CpuTimeCallableOperation<V> extends SimpleMonitorableOperation implements Callable<V>
{

    private Object lock = new Object();

    public CpuTimeCallableOperation()
    {
        super(Type.CPU_TIME);
    }

    public V start() throws Exception
    {
        synchronized (lock)
        {
            getCounter().setUnitsAfter(0);
            getCounter()
                    .setUnitsBefore(getCounter().getTimeUnit().fromNanoseconds(PerformetricsUtils.getCpuTimeNanos()));
            try
            {
                return call();
            }
            finally
            {
                getCounter().setUnitsAfter(
                        getCounter().getTimeUnit().fromNanoseconds(PerformetricsUtils.getCpuTimeNanos()));
            }
        }
    }

}
