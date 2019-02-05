package net.obvj.performetrics;

/**
 * A base object for monitorable operations that require a single counter.
 *
 * @author oswaldo.bapvic.jr
 */
public abstract class SimpleMonitorableOperation
{
    protected final Counter counter;

    public SimpleMonitorableOperation(Counter.Type type)
    {
        this.counter = new Counter(type);
    }

    public Counter getCounter()
    {
        return counter;
    }

}
