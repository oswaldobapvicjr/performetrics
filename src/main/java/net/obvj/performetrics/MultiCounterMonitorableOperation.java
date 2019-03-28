package net.obvj.performetrics;

import java.util.Collection;

import net.obvj.performetrics.Counter.Type;

/**
 * A base object for monitorable operations that require more than one counter.
 *
 * @author oswaldo.bapvic.jr
 */
public abstract class MultiCounterMonitorableOperation
{
    protected final Stopwatch stopwatch;

    /**
     * Builds this operation with a the specified types, which will be maintained using
     * default time unit.
     *
     * @param types the counter types to created
     */
    public MultiCounterMonitorableOperation(Type... types)
    {
        if (types.length > 0)
            this.stopwatch = new Stopwatch(types);
        else
            this.stopwatch = new Stopwatch();
    }

    /**
     * @return all counters maintained for this operation
     */
    public Collection<Counter> getCounters()
    {
        return stopwatch.getAllCounters();
    }

    /**
     * @param type the counter type to be fetched
     * @return the counter object associated with the given type for this operation
     */
    public Counter getCounter(Type type)
    {
        return stopwatch.getCounter(type);
    }

}
