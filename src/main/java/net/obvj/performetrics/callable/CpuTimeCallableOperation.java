package net.obvj.performetrics.callable;

import static java.util.concurrent.TimeUnit.NANOSECONDS;

import java.util.concurrent.Callable;

import net.obvj.performetrics.Counter.Type;
import net.obvj.performetrics.SimpleMonitorableOperation;
import net.obvj.performetrics.util.PerformetricsUtils;

/**
 * A {@link Callable} that maintains a CPU time counter for elapsed time evaluation
 * <p>
 * The CPU time is stored in nanoseconds.
 *
 * @param <V> the result type of method <tt>call</tt>
 * @author oswaldo.bapvic.jr
 */
public abstract class CpuTimeCallableOperation<V> extends SimpleMonitorableOperation implements Callable<V>
{

    private Object lock = new Object();

    public CpuTimeCallableOperation()
    {
        super(Type.CPU_TIME, NANOSECONDS);
    }

    public V start() throws Exception
    {
        synchronized (lock)
        {
            getCounter().setUnitsAfter(0);
            getCounter().setUnitsBefore(PerformetricsUtils.getCpuTimeNanos());
            try
            {
                return call();
            }
            finally
            {
                getCounter().setUnitsAfter(PerformetricsUtils.getCpuTimeNanos());
            }
        }
    }

}
