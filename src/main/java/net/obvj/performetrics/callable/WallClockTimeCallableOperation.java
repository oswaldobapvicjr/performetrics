package net.obvj.performetrics.callable;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.util.concurrent.Callable;

import net.obvj.performetrics.Counter.Type;
import net.obvj.performetrics.SimpleMonitorableOperation;
import net.obvj.performetrics.util.PerformetricsUtils;

/**
 * @param <V> the result type of method <tt>call</tt>
 * @author oswaldo.bapvic.jr
 */
public abstract class WallClockTimeCallableOperation<V> extends SimpleMonitorableOperation implements Callable<V>
{

    private V result = null;

    private Object lock = new Object();

    public WallClockTimeCallableOperation()
    {
        super(Type.WALL_CLOCK_TIME, MILLISECONDS);
    }

    public V start() throws Exception
    {
        synchronized (lock)
        {
            getCounter().setUnitsAfter(0);
            getCounter().setUnitsBefore(
                    getCounter().getTimeUnit().convert(PerformetricsUtils.getWallClockTimeMillis(), MILLISECONDS));
            try
            {
                result = call();
            }
            finally
            {
                getCounter().setUnitsAfter(
                        getCounter().getTimeUnit().convert(PerformetricsUtils.getWallClockTimeMillis(), MILLISECONDS));
            }
        }
        return result;
    }

    public V get()
    {
        return result;
    }

}
