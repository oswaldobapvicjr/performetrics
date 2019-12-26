package net.obvj.performetrics;

import java.io.PrintStream;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import net.obvj.performetrics.Counter.Type;
import net.obvj.performetrics.util.printer.PrintUtils;

/**
 * A convenient object for timings that support multiple counter types
 * <p>
 * This class is not thread-safe.
 *
 * @author oswaldo.bapvic.jr
 */
public class Stopwatch
{
    private static final String MSG_PATTERN_TYPE_NOT_AVAILABLE = "\"{0}\" is not available in this instance. Available type(s): {1}";

    private static final Type[] DEFAULT_TYPES = Type.values();

    private final Type[] types;
    private Map<Type, Counter> counters;

    /**
     * Creates a new stopwatch with default counter types
     */
    public Stopwatch()
    {
        this(DEFAULT_TYPES);
    }

    /**
     * Creates a new stopwatch with specific counter types
     *
     * @param types the types to be set
     */
    public Stopwatch(Type... types)
    {
        this.types = types;
        reset();
    }

    /**
     * Provides a started stopwatch for convenience with default counter types
     *
     * @return a new, started stopwatch
     */
    public static Stopwatch createStarted()
    {
        return createStarted(DEFAULT_TYPES);
    }

    /**
     * Provides a started stopwatch for convenience with specific counter types
     *
     * @param types the types to be set
     * @return a new, started stopwatch
     */
    public static Stopwatch createStarted(Type... types)
    {
        Stopwatch stopwatch = new Stopwatch(types);
        stopwatch.start();
        return stopwatch;
    }

    /**
     * Resets all counters for this stopwatch
     */
    public void reset()
    {
        counters = new EnumMap<>(Type.class);
        for (Type type : types)
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
            counter.before();
        }
    }

    /**
     * Stops the stopwatch
     */
    public void stop()
    {
        for (Counter counter : counters.values())
        {
            counter.after();
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
     * @throws IllegalArgumentException if the specified type is not available
     */
    public Counter getCounter(Type type)
    {
        if (!counters.containsKey(type))
        {
            throw new IllegalArgumentException(
                    MessageFormat.format(MSG_PATTERN_TYPE_NOT_AVAILABLE, type, counters.keySet()));
        }
        return counters.get(type);
    }

    /**
     * Prints stopwatch statistics in the specified print stream.
     *
     * @param printStream the print stream to which statistics will be sent
     */
    public void printStatistics(PrintStream printStream)
    {
        PrintUtils.printStopwatch(this, printStream);
    }

    /**
     * Prints stopwatch statistics in the specified print stream, with a custom time unit.
     *
     * @param printStream the print stream to which statistics will be sent
     * @param timeUnit    the time unit for the elapsed times to be displayed
     */
    public void printStatistics(PrintStream printStream, TimeUnit timeUnit)
    {
        PrintUtils.printStopwatch(this, printStream, timeUnit);
    }

}
