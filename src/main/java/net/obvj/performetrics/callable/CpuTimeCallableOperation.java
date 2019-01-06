package net.obvj.performetrics.callable;

import java.util.concurrent.Callable;

import net.obvj.performetrics.BaseTimedOperation;
import net.obvj.performetrics.util.PerfrometricsUtils;

/**
 * @author oswaldo.bapvic.jr
 */
public abstract class CpuTimeCallableOperation<V> extends BaseTimedOperation implements Callable<V>
{

    private Object lock = new Object();

    public V start() throws Exception
    {
        synchronized (lock)
        {
            timeAfter = 0;
            timeBefore = (PerfrometricsUtils.getCpuTimeNanos() / 1000000) % 1000000;
            try
            {
                return call();
            }
            finally
            {
                timeAfter = (PerfrometricsUtils.getCpuTimeNanos() / 1000000) % 1000000;
            }
        }
    }

}
