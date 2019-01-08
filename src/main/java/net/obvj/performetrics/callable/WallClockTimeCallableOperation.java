package net.obvj.performetrics.callable;

import java.util.concurrent.Callable;

import net.obvj.performetrics.SimpleMonitorableOperation;
import net.obvj.performetrics.UnitType;
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
        super(UnitType.WALL_CLOCK_TIME);
    }

    public V start() throws Exception
    {
        synchronized (lock)
        {
            getCounter().setUnitsAfter(0);
            getCounter().setUnitsBefore(PerformetricsUtils.getWallClockTimeNanos());
            try
            {
                result = call();
            }
            finally
            {
                getCounter().setUnitsAfter(PerformetricsUtils.getWallClockTimeNanos());
            }
        }
        return result;
    }

    public V get()
    {
        return result;
    }

}
