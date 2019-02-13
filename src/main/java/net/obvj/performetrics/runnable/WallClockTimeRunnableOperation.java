package net.obvj.performetrics.runnable;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import net.obvj.performetrics.Counter.Type;
import net.obvj.performetrics.SimpleMonitorableOperation;
import net.obvj.performetrics.util.PerformetricsUtils;

/**
 * A {@link Runnable} that maintains a wall-clock time counter for elapsed time evaluation
 * <p>
 * The wall-clock time is stored in milliseconds.
 *
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
            getCounter().setUnitsBefore(PerformetricsUtils.getWallClockTimeMillis());
            try
            {
                run();
            }
            finally
            {
                getCounter().setUnitsAfter(PerformetricsUtils.getWallClockTimeMillis());
            }
        }
    }

}
