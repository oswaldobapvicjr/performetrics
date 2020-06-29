package net.obvj.performetrics.monitors;

import java.io.PrintStream;
import java.util.Collection;
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
     * @throws IllegalArgumentException if the specified type is not available in this
     *                                  operation
     */
    public Counter getCounter(Type type)
    {
        return stopwatch.getCounter(type);
    }

    /**
     * A convenient method that returns the elapsed time of a specific counter.
     * <p>
     * This has the same effect as calling: {@code operation.getCounter(type).elapsedTime()}
     *
     * @param type the counter type to be fetched
     * @return the elapsed time for the specified counter
     * @throws IllegalArgumentException if the specified type is not available in this
     *                                  operation
     * @since 2.1.0
     */
    public Duration elapsedTime(Type type)
    {
        return getCounter(type).elapsedTime();
    }

    /**
     * A convenient method that returns the elapsed time of a specific counter, in the
     * specified time unit.
     * <p>
     * This has the same effect as calling:
     * {@code operation.getCounter(type).elapsedTime(timeUnit)}
     *
     * @param type     the counter type to be fetched
     * @param timeUnit the time unit to which the elapsed time will be converted
     * @return the elapsed time for the specified counter, converted to the given time unit
     *         using the default conversion mode.
     * @throws IllegalArgumentException if the specified type is not available in this
     *                                  operation
     * @since 2.1.0
     */
    public double elapsedTime(Type type, TimeUnit timeUnit)
    {
        return getCounter(type).elapsedTime(timeUnit);
    }

    /**
     * A convenient method that returns the elapsed time of a specific counter, in the
     * specified time unit, by applying a custom {@link ConversionMode}.
     * <p>
     * This has the same effect as calling:
     * {@code operation.getCounter(type).elapsedTime(timeUnit, conversionMode)}
     *
     * @param type           the counter type to be fetched
     * @param timeUnit       the time unit to which the elapsed time will be converted
     * @param conversionMode the {@link ConversionMode} to be applied
     * @return the elapsed time for the specified counter, converted to the given time unit
     *         using the given conversion mode.
     * @throws IllegalArgumentException if the specified type is not available in this
     *                                  operation
     * @since 2.1.0
     */
    public double elapsedTime(Type type, TimeUnit timeUnit, ConversionMode conversionMode)
    {
        return getCounter(type).elapsedTime(timeUnit, conversionMode);
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
