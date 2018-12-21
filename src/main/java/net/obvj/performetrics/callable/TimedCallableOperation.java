package net.obvj.performetrics.callable;

import java.util.concurrent.Callable;

import net.obvj.performetrics.BaseTimedOperation;

/**
 * @param <V> the result type of method <tt>call</tt>
 * @author oswaldo.bapvic.jr
 */
public abstract class TimedCallableOperation<V> extends BaseTimedOperation implements Callable<V>
{

    private V result = null;

    private Object lock = new Object();

    public V start() throws Exception
    {
        synchronized (lock)
        {
            timeAfter = 0;
            timeBefore = System.currentTimeMillis();
            try
            {
                result = call();
            }
            finally
            {
                timeAfter = System.currentTimeMillis();
            }
        }
        return result;
    }

    public V get()
    {
        return result;
    }

}
