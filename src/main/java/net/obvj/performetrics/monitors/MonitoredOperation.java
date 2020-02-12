package net.obvj.performetrics.monitors;

import java.io.PrintStream;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import net.obvj.performetrics.Counter;
import net.obvj.performetrics.Stopwatch;
import net.obvj.performetrics.Counter.Type;
import net.obvj.performetrics.util.printer.PrintUtils;

/**
 * A base object for monitorable operations that maintains one or more counters.
 *
 * @author oswaldo.bapvic.jr
 */
abstract class MonitoredOperation
{
    protected static final Type[] NO_SPECIFIC_TYPE = new Type[0];

    protected final Stopwatch stopwatch;

    /**
     * Builds a monitored operation with the specified type(s).
     *
     * @param types the counter type(s) to be maintained by this operation
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
     * Returns the counters maintained by this monitored operation.
     *
     * @return all counters maintained by this monitored operation.
     */
    public Collection<Counter> getCounters()
    {
        return stopwatch.getCounters();
    }

    /**
     * Returns the counter instance associated with a given type in this monitored operation.
     *
     * @param type the counter type to be fetched
     * @return the counter instance associated with the given type in this monitored operation
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
        PrintUtils.printCounters(stopwatch.getCounters(), printStream);
    }

    /**
     * Prints operation statistics in the specified print stream, with a custom time unit.
     *
     * @param printStream the print stream to which statistics will be sent
     * @param timeUnit    the time unit for the elapsed times to be displayed
     */
    public void printStatistics(PrintStream printStream, TimeUnit timeUnit)
    {
        PrintUtils.printCounters(stopwatch.getCounters(), printStream, timeUnit);
    }

}
