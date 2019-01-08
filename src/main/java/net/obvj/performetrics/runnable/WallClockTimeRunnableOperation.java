package net.obvj.performetrics.runnable;

import net.obvj.performetrics.SimpleMonitorableOperation;
import net.obvj.performetrics.UnitType;
import net.obvj.performetrics.util.PerformetricsUtils;

/**
 * @author oswaldo.bapvic.jr
 */
public abstract class WallClockTimeRunnableOperation extends SimpleMonitorableOperation implements Runnable
{

    private Object lock = new Object();

    public WallClockTimeRunnableOperation()
    {
        super(UnitType.WALL_CLOCK_TIME);
    }

    public void start()
    {
        synchronized (lock)
        {
            unitsAfter = 0;
            unitsBefore = PerformetricsUtils.getWallClockTimeNanos();
            try
            {
                run();
            }
            finally
            {
                unitsAfter = PerformetricsUtils.getWallClockTimeNanos();
            }
        }
    }

}
