package net.obvj.performetrics.monitors;

import java.io.PrintStream;
import java.util.List;
import java.util.concurrent.TimeUnit;

import net.obvj.performetrics.ConversionMode;
import net.obvj.performetrics.Counter;
import net.obvj.performetrics.Counter.Type;
import net.obvj.performetrics.Stopwatch;
import net.obvj.performetrics.util.Duration;
import net.obvj.performetrics.util.printer.PrintUtils;

/**
 * A base object for monitorable operations that maintains one or more counters.
 *
 * @author oswaldo.bapvic.jr
 */
abstract class MonitoredOperation
{
    protected static final Type[] NO_SPECIFIC_TYPE = new Type[0];

    protected Stopwatch stopwatch;

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
     * Returns the counter types associated with this monitored operation.
     *
     * @return all counter types associated with this monitored operation
     */
    protected List<Type> getTypes()
    {
        return stopwatch.getTypes();
    }

    /**
     * Returns all counters available in this monitored operation.
     *
     * @return all counters available in this monitored operation.
     */
    public List<Counter> getCounters()
    {
        return stopwatch.getCounters();
    }

    /**
     * Returns all counters associated with a given type in this monitored operation.
     *
     * @param type the counter type to be fetched
     * @return all counters associated with the given type
     * @throws IllegalArgumentException if the type was not specified in this operation
     */
    public List<Counter> getCounters(Type type)
    {
        return stopwatch.getCounters(type);
    }

    /**
     * Returns the total elapsed time of a specific counter type.
     *
     * @param type the counter type to be fetched
     * @return the total elapsed time for the specified counter
     * @throws IllegalArgumentException if the type was not specified in this operation
     * @since 2.1.0
     */
    public Duration elapsedTime(Type type)
    {
        return stopwatch.elapsedTime(type);
    }

    /**
     * Returns the total elapsed time of a specific counter type, in the specified time unit.
     *
     * @param type     the counter type to be fetched
     * @param timeUnit the time unit to which the elapsed time will be converted
     * @return the elapsed time for the specified counter, converted to the given time unit
     *         using the default conversion mode.
     * @throws IllegalArgumentException if the type was not specified in this operation
     * @since 2.1.0
     */
    public double elapsedTime(Type type, TimeUnit timeUnit)
    {
        return stopwatch.elapsedTime(type, timeUnit);
    }

    /**
     * Returns the total elapsed time of a specific counter, in the specified time unit, with
     * a custom {@link ConversionMode} applied.
     *
     * @param type           the counter type to be fetched
     * @param timeUnit       the time unit to which the elapsed time will be converted
     * @param conversionMode the {@link ConversionMode} to be applied
     * @return the elapsed time for the specified counter, converted to the given time unit
     *         using the given conversion mode.
     * @throws IllegalArgumentException if the type was not specified in this operation
     * @since 2.1.0
     */
    public double elapsedTime(Type type, TimeUnit timeUnit, ConversionMode conversionMode)
    {
        return stopwatch.elapsedTime(type, timeUnit, conversionMode);
    }

    /**
     * Prints operation statistics in the specified print stream.
     *
     * @param printStream the print stream to which statistics will be sent
     */
    public void printStatistics(PrintStream printStream)
    {
        PrintUtils.print(stopwatch.getCounters(), printStream);
    }

    /**
     * Prints operation statistics in the specified print stream, with a custom time unit.
     *
     * @param printStream the print stream to which statistics will be sent
     * @param timeUnit    the time unit for the elapsed times to be displayed
     */
    public void printStatistics(PrintStream printStream, TimeUnit timeUnit)
    {
        PrintUtils.print(stopwatch.getCounters(), printStream, timeUnit);
    }

    /**
     * Cleans all counters available in this monitored operation.
     *
     * @since 2.2.0
     */
    public void reset()
    {
        stopwatch.reset();
    }

}
