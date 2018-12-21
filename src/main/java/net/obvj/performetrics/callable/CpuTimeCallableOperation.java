package net.obvj.performetrics.callable;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.concurrent.Callable;

import net.obvj.performetrics.BaseTimedOperation;

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
            timeBefore = (getCpuTime() / 1000000) % 1000000;
            try
            {
                return call();
            }
            finally
            {
                timeAfter = (getCpuTime() / 1000000) % 1000000;
            }
        }
    }

    // TODO: Extract these method to a common class for all operations

    /** Get CPU time in nanoseconds. */
    public long getCpuTime()
    {
        ThreadMXBean bean = ManagementFactory.getThreadMXBean();
        return bean.isCurrentThreadCpuTimeSupported() ? bean.getCurrentThreadCpuTime() : 0L;
    }

    /** Get user time in nanoseconds. */
    public long getUserTime()
    {
        ThreadMXBean bean = ManagementFactory.getThreadMXBean();
        return bean.isCurrentThreadCpuTimeSupported() ? bean.getCurrentThreadUserTime() : 0L;
    }

    /** Get system time in nanoseconds. */
    public long getSystemTime()
    {
        ThreadMXBean bean = ManagementFactory.getThreadMXBean();
        return bean.isCurrentThreadCpuTimeSupported()
                ? (bean.getCurrentThreadCpuTime() - bean.getCurrentThreadUserTime())
                : 0L;
    }

}
