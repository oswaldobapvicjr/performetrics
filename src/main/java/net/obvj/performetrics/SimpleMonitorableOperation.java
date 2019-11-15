package net.obvj.performetrics;

import java.util.concurrent.TimeUnit;

import net.obvj.performetrics.Counter.Type;

/**
 * A base object for monitorable operations that require a single counter.
 *
 * @author oswaldo.bapvic.jr
 */
public abstract class SimpleMonitorableOperation
{
    protected final Counter counter;

    /**
     * Builds this operation with a new counter of the specified type and time unit.
     *
     * @param type the counter type to created
     * @param timeUnit the time unit to be set
     */
    public SimpleMonitorableOperation(Type type, TimeUnit timeUnit)
    {
        this.counter = new Counter(type, timeUnit);
    }

    /**
     * @return the counter object for this operation
     */
    public Counter getCounter()
    {
        return counter;
    }

    /**
     * @return the time unit set for the counter object maintained by this operation
     */
    public TimeUnit getTimeUnit()
    {
        return counter.getDefaultTimeUnit();
    }

}
