package net.obvj.performetrics;

/**
 * @author oswaldo.bapvic.jr
 */
public abstract class SimpleMonitorableOperation
{
    protected long unitsBefore = 0;
    protected long unitsAfter = 0;
    protected UnitType unitType;

    public SimpleMonitorableOperation(UnitType unitType)
    {
        this.unitType = unitType;
    }

    public long getElapsedTimeNanos()
    {
        return unitsAfter >= unitsBefore ? (unitsAfter - unitsBefore) : -1;
    }

    public long getElapsedTimeMillis()
    {
        long elapsedTimeNanos = getElapsedTimeNanos();
        return elapsedTimeNanos > 0 ? (getElapsedTimeNanos() / 1000000) % 1000000 : -1;
    }

    public double getElapsedTimeSeconds()
    {
        long elapsedTimeMilis = getElapsedTimeMillis();
        return elapsedTimeMilis > 0 ? elapsedTimeMilis / 1000 : -1;
    }

}
