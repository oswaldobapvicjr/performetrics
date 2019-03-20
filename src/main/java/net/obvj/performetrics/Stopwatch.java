package net.obvj.performetrics;

import java.io.PrintStream;
import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;

import net.obvj.performetrics.Counter.Type;
import net.obvj.performetrics.util.printer.PrintUtils;

/**
 * A convenient object for timings that support multiple counter types.
 * <p>
 * This class is not thread-safe.
 *
 * @author oswaldo.bapvic.jr
 */
public class Stopwatch
{
    private Map<Type, Counter> counters;

    /**
     * Creates a new stopwatch
     */
    public Stopwatch()
    {
        reset();
    }

    /**
     * Provides a started stopwatch for convenience.
     *
     * @return a new, started stopwatch
     */
    public static Stopwatch createStarted()
    {
        Stopwatch stopwatch = new Stopwatch();
        stopwatch.start();
        return stopwatch;
    }

    /**
     * Resets all counters for this stopwatch
     */
    public void reset()
    {
        counters = new EnumMap<>(Type.class);
        for (Type type : Type.values())
        {
        	counters.put(type, new Counter(type));
        }
    }

    /**
     * Starts the stopwatch
     */
    public void start()
    {
        for (Counter counter : counters.values())
        {
            counter.setUnitsBefore(counter.getType().defaultDataFetchStrategy(counter.getTimeUnit()));
        }
    }

    /**
     * Stops the stopwatch
     */
    public void stop()
    {
        for (Counter counter : counters.values())
        {
            counter.setUnitsAfter(counter.getType().defaultDataFetchStrategy(counter.getTimeUnit()));
        }
    }

    /**
     * @return all counters in this stopwatch
     */
    public Collection<Counter> getAllCounters()
    {
        return counters.values();
    }

    /**
     * @param type the counter type to be fetched
     * @return the counter matching the given type inside this stopwatch
     */
    public Counter getCounter(Type type)
    {
        return counters.get(type);
    }

    /**
     * Prints the statistics for this counter in the specified print stream.
     *
     * @param printStream the print stream to which statistics will be sent
     */
    public void printStatistics(PrintStream printStream)
    {
        PrintUtils.printStopwatch(this, printStream);
    }

}
