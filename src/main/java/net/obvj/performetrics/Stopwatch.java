package net.obvj.performetrics;

import java.io.PrintStream;
import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import net.obvj.performetrics.Counter.Type;
import net.obvj.performetrics.util.PerformetricsUtils;

/**
 * A convenient object for timings that support multiple counter types.
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
     * Resets all counters for this stopwatch.
     */
    public void reset()
    {
        counters = new EnumMap<>(Type.class);
        counters.put(Type.WALL_CLOCK_TIME, new Counter(Type.WALL_CLOCK_TIME, TimeUnit.NANOSECONDS));
        counters.put(Type.CPU_TIME, new Counter(Type.CPU_TIME, TimeUnit.NANOSECONDS));
        counters.put(Type.USER_TIME, new Counter(Type.USER_TIME, TimeUnit.NANOSECONDS));
        counters.put(Type.SYSTEM_TIME, new Counter(Type.SYSTEM_TIME, TimeUnit.NANOSECONDS));
    }

    /**
     * Starts the stopwatch
     */
    public void start()
    {
        counters.get(Type.WALL_CLOCK_TIME).setUnitsBefore(PerformetricsUtils.getWallClockTimeNanos());
        counters.get(Type.CPU_TIME).setUnitsBefore(PerformetricsUtils.getCpuTimeNanos());
        counters.get(Type.USER_TIME).setUnitsBefore(PerformetricsUtils.getUserTimeNanos());
        counters.get(Type.SYSTEM_TIME).setUnitsBefore(PerformetricsUtils.getSystemTimeNanos());
    }

    /**
     * Stops the stopwatch
     */
    public void stop()
    {
        counters.get(Type.WALL_CLOCK_TIME).setUnitsAfter(PerformetricsUtils.getWallClockTimeNanos());
        counters.get(Type.CPU_TIME).setUnitsAfter(PerformetricsUtils.getCpuTimeNanos());
        counters.get(Type.USER_TIME).setUnitsAfter(PerformetricsUtils.getUserTimeNanos());
        counters.get(Type.SYSTEM_TIME).setUnitsAfter(PerformetricsUtils.getSystemTimeNanos());
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
        String rowFormat = "\n| %-15s | %20s | %-12s |";
        StringBuilder builder = new StringBuilder();
        String header = String.format(rowFormat, "Counter", "Elapsed time", "Time unit");
        String separator = String.format(rowFormat, "", "", "").replace(" ", "-").replace("|", "+");
        builder.append(separator);
        builder.append(header);
        builder.append(separator);
        for (Counter counter : counters.values())
        {
            builder.append(String.format(rowFormat, counter.getType(), counter.elapsedTime(), counter.getTimeUnit()));
        }
        builder.append(separator);
        builder.append("\n");
        printStream.print(builder.toString());
    }

    @Override
    public String toString()
    {
        return String.format("Stopwatch [counters=%s]", counters);
    }

}
