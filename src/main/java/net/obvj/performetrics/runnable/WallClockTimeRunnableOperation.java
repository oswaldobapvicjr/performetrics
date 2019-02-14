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
public class WallClockTimeRunnableOperation extends SimpleMonitorableOperation implements Runnable
{
    private Runnable targetRunnable;
    private Object lock = new Object();

    public WallClockTimeRunnableOperation(Runnable targetRunnable)
    {
        super(Type.WALL_CLOCK_TIME, MILLISECONDS);
        this.targetRunnable = targetRunnable;
    }

    @Override
    public void run()
    {
        synchronized (lock)
        {
            counter.setUnitsAfter(0);
            counter.setUnitsBefore(PerformetricsUtils.getWallClockTimeMillis());
            try
            {
                targetRunnable.run();
            }
            finally
            {
                counter.setUnitsAfter(PerformetricsUtils.getWallClockTimeMillis());
            }
        }
    }

}
