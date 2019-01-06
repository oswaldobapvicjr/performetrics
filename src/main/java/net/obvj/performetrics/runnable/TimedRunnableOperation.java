package net.obvj.performetrics.runnable;

import net.obvj.performetrics.BaseTimedOperation;
import net.obvj.performetrics.util.PerfrometricsUtils;

/**
 * @author oswaldo.bapvic.jr
 */
public abstract class TimedRunnableOperation extends BaseTimedOperation implements Runnable
{

    private Object lock = new Object();

    public void start()
    {
        synchronized (lock)
        {
            timeAfter = 0;
            timeBefore = PerfrometricsUtils.getWallClockTimeMillis();
            try
            {
                run();
            }
            finally
            {
                timeAfter = PerfrometricsUtils.getWallClockTimeMillis();
            }
        }
    }

}
