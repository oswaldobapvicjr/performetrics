package net.obvj.performetrics.callable;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.util.concurrent.Callable;

import net.obvj.performetrics.Counter.Type;
import net.obvj.performetrics.SimpleMonitorableOperation;
import net.obvj.performetrics.util.PerformetricsUtils;

/**
 * A {@link Callable} that maintains a wall-clock time counter for elapsed time evaluation
 * <p>
 * The wall-clock time is stored in milliseconds.
 *
 * @param <V> the result type of method call
 * @author oswaldo.bapvic.jr
 */
public class WallClockTimeCallableOperation<V> extends SimpleMonitorableOperation implements Callable<V>
{
    private Callable<V> targetCallable;
    private Object lock = new Object();

    /**
     * Builds this monitorable operation with a given {@link Callable} to be profiled using
     * wall-clock time.
     *
     * @param targetCallable the {@link Callable} to be executed and profiled
     */
    public WallClockTimeCallableOperation(Callable<V> targetCallable)
    {
        super(Type.WALL_CLOCK_TIME, MILLISECONDS);
        this.targetCallable = targetCallable;
    }

    /**
     * See {@link Callable#call()}.
     */
    @Override
    public V call() throws Exception
    {
        synchronized (lock)
        {
            counter.setUnitsAfter(0);
            counter.setUnitsBefore(PerformetricsUtils.getWallClockTimeMillis());
            try
            {
                return targetCallable.call();
            }
            finally
            {
                counter.setUnitsAfter(PerformetricsUtils.getWallClockTimeMillis());
            }
        }
    }

}
