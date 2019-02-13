package net.obvj.performetrics.runnable;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.util.concurrent.TimeUnit;

import net.obvj.performetrics.Counter.Type;
import net.obvj.performetrics.SimpleMonitorableOperation;
import net.obvj.performetrics.util.PerformetricsUtils;

/**
 * @author oswaldo.bapvic.jr
 */
public abstract class WallClockTimeRunnableOperation extends SimpleMonitorableOperation implements Runnable
{

    private Object lock = new Object();

    public WallClockTimeRunnableOperation()
    {
        super(Type.WALL_CLOCK_TIME, MILLISECONDS);
    }

    public void start()
    {
        synchronized (lock)
        {
            getCounter().setUnitsAfter(0);
            getCounter().setUnitsBefore(
                    getCounter().getTimeUnit().convert(PerformetricsUtils.getWallClockTimeMillis(), MILLISECONDS));
            try
            {
                run();
            }
            finally
            {
                getCounter().setUnitsAfter(getCounter().getTimeUnit()
                        .convert(PerformetricsUtils.getWallClockTimeMillis(), TimeUnit.MILLISECONDS));
            }
        }
    }

}
