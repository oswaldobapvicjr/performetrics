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
 * @param <V> the result type of method call
 * @author oswaldo.bapvic.jr
 */
public class CpuTimeCallableOperation<V> extends SimpleMonitorableOperation implements Callable<V>
{
    private Callable<V> targetCallable;

    /**
     * Builds this monitorable operation with a given {@link Callable} to be profiled using
     * CPU time.
     *
     * @param targetCallable the {@link Callable} to be executed and profiled
     */
    public CpuTimeCallableOperation(Callable<V> targetCallable)
    {
        super(Type.CPU_TIME, NANOSECONDS);
        this.targetCallable = targetCallable;
    }

    /**
     * See {@link Callable#call()}.
     */
    @Override
    public V call() throws Exception
    {
        counter.setUnitsAfter(0);
        counter.setUnitsBefore(PerformetricsUtils.getCpuTimeNanos());
        try
        {
            return targetCallable.call();
        }
        finally
        {
            counter.setUnitsAfter(PerformetricsUtils.getCpuTimeNanos());
        }
    }

}
