package net.obvj.performetrics;

/**
 * @author oswaldo.bapvic.jr
 */
public abstract class BaseTimedOperation
{

    protected long timeBefore = 0;
    protected long timeAfter = 0;

    public long getElapsedTimeMillis()
    {
        return timeAfter >= timeBefore ? (timeAfter - timeBefore) : -1;
    }

    public double getElapsedTimeSeconds()
    {
        return timeAfter >= timeBefore ? (timeAfter - timeBefore) / 1000d : -1;
    }

}
