package net.obvj.performetrics.runnable;

import net.obvj.performetrics.BaseTimedOperation;

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
            timeBefore = System.currentTimeMillis();
            try
            {
                run();
            }
            finally
            {
                timeAfter = System.currentTimeMillis();
            }
        }
    }

}
