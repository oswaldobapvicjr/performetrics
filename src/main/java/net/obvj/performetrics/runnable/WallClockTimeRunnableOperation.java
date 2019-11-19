package net.obvj.performetrics.runnable;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import net.obvj.performetrics.Counter.Type;
import net.obvj.performetrics.SimpleMonitorableOperation;
import net.obvj.performetrics.util.PerformetricsUtils;

/**
 * A {@link Runnable} that maintains a wall-clock time counter for elapsed time evaluation
 * <p>
 * The wall-clock time is stored in milliseconds.
 * <p>
 * <b>Note:</b> Not thread-safe. It is recommended to create separate instances for each
 * thread. If multiple threads access the same instance of this class, the metrics may be
 * inconsistent.
 *
 * @author oswaldo.bapvic.jr
 */
public class WallClockTimeRunnableOperation extends SimpleMonitorableOperation implements Runnable
{
    private Runnable targetRunnable;

    /**
     * Builds this monitorable operation with a given {@link Runnable} to be profiled using
     * wall-clock time.
     * 
     * @param targetRunnable the {@link Runnable} to be executed and profiled
     */
    public WallClockTimeRunnableOperation(Runnable targetRunnable)
    {
        super(Type.WALL_CLOCK_TIME, MILLISECONDS);
        this.targetRunnable = targetRunnable;
    }

    /**
     * See {@link Runnable#run()}.
     */
    @Override
    public void run()
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
