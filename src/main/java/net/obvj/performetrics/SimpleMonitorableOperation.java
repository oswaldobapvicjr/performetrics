package net.obvj.performetrics;

/**
 * A base object for monitorable operations that require a single counter.
 *
 * @author oswaldo.bapvic.jr
 */
public abstract class SimpleMonitorableOperation
{
    protected final Counter counter;

    public SimpleMonitorableOperation(UnitType unitType)
    {
        this.counter = new Counter(unitType);
    }

    public Counter getCounter()
    {
        return counter;
    }

}
