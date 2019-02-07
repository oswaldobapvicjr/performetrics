package net.obvj.performetrics.runnable;

import net.obvj.performetrics.Counter.Type;
import net.obvj.performetrics.SimpleMonitorableOperation;
import net.obvj.performetrics.TimeUnit;
import net.obvj.performetrics.util.PerformetricsUtils;

/**
 * @author oswaldo.bapvic.jr
 */
public abstract class WallClockTimeRunnableOperation extends SimpleMonitorableOperation implements Runnable
{

    private Object lock = new Object();

    public WallClockTimeRunnableOperation()
    {
        super(Type.WALL_CLOCK_TIME, TimeUnit.MILLISECOND);
    }

    public void start()
    {
        synchronized (lock)
        {
            getCounter().setUnitsAfter(0);
            getCounter().setUnitsBefore(
                    getCounter().getTimeUnit().fromMilliseconds(PerformetricsUtils.getWallClockTimeMillis()));
            try
            {
                run();
            }
            finally
            {
                getCounter().setUnitsAfter(
                        getCounter().getTimeUnit().fromMilliseconds(PerformetricsUtils.getWallClockTimeMillis()));
            }
        }
    }

}
