package net.obvj.performetrics;

import java.io.PrintStream;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import net.obvj.performetrics.Counter.Type;
import net.obvj.performetrics.util.printer.PrintUtils;

/**
 * A base object for monitorable operations that require one or more counters.
 *
 * @author oswaldo.bapvic.jr
 */
public abstract class MonitoredOperation
{
    protected static final Type[] NO_SPECIFIC_TYPE = new Type[0];

    protected final Stopwatch stopwatch;

    /**
     * Builds this operation with the specified type(s), which will be maintained in the
     * default time unit.
     *
     * @param types the counter types to created
     */
    public MonitoredOperation(Type... types)
    {
        if (types.length > 0)
        {
            this.stopwatch = new Stopwatch(types);
        }
        else
        {
            this.stopwatch = new Stopwatch();
        }
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

    /**
     * Prints operation statistics in the specified print stream.
     *
     * @param printStream the print stream to which statistics will be sent
     */
    public void printStatistics(PrintStream printStream)
    {
        PrintUtils.printCounters(stopwatch.getAllCounters(), printStream);
    }

    /**
     * Prints operation statistics in the specified print stream, with a custom time unit.
     *
     * @param printStream the print stream to which statistics will be sent
     * @param timeUnit    the time unit for the elapsed times to be displayed
     */
    public void printStatistics(PrintStream printStream, TimeUnit timeUnit)
    {
        PrintUtils.printCounters(stopwatch.getAllCounters(), printStream, timeUnit);
    }

}
