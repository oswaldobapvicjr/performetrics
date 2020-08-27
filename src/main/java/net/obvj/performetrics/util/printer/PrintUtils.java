package net.obvj.performetrics.util.printer;

import java.io.PrintStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import net.obvj.performetrics.Counter;
import net.obvj.performetrics.Counter.Type;
import net.obvj.performetrics.Stopwatch;
import net.obvj.performetrics.config.ConfigurationHolder;

/**
 * This class groups all custom printing operations in a single place.
 *
 * @author oswaldo.bapvic.jr
 */
public class PrintUtils
{
    protected static final String LINE_SEPARATOR = System.getProperty("line.separator");

    protected static final String COUNTERS_TABLE_COLUMN_TIME_UNIT = "Time unit";
    protected static final String COUNTERS_TABLE_COLUMN_ELAPSED_TIME = "Elapsed time";
    protected static final String COUNTERS_TABLE_COLUMN_COUNTER = "Counter";

    protected static final String COUNTERS_TABLE_ROW_FORMAT = LINE_SEPARATOR + "| %-15s | %20s | %-12s |";
    protected static final String COUNTERS_TABLE_ROW_SEPARATOR = String.format(COUNTERS_TABLE_ROW_FORMAT, "", "", "")
            .replace(" ", "-").replace("|", "+");

    protected static final String COUNTERS_TABLE_HEADER = String.format(COUNTERS_TABLE_ROW_FORMAT,
            COUNTERS_TABLE_COLUMN_COUNTER, COUNTERS_TABLE_COLUMN_ELAPSED_TIME, COUNTERS_TABLE_COLUMN_TIME_UNIT);

    private static final String ELAPSED_TIME_FORMAT = "#.################";

    /**
     * This is a utility class, not meant to be instantiated.
     */
    private PrintUtils()
    {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Prints the statistics for the given stopwatch in the specified print stream.
     *
     * @param stopwatch   the stopwatch to be printed
     * @param printStream the print stream to which statistics will be sent
     * @throws NullPointerException if a null stopwatch or print stream is received
     */
    public static void print(Stopwatch stopwatch, PrintStream printStream)
    {
        print(stopwatch, printStream, null);
    }

    /**
     * Prints the statistics for the given stopwatch in the specified print stream.
     *
     * @param stopwatch   the stopwatch to be printed
     * @param printStream the print stream to which statistics will be sent
     * @param timeUnit    the time unit in which elapsed times will be displayed; if null, the
     *                    default time unit specified for each counter will be applied
     * @throws NullPointerException if a null stopwatch or print stream is received
     */
    public static void print(Stopwatch stopwatch, PrintStream printStream, TimeUnit timeUnit)
    {
        print(groupCountersByType(stopwatch), printStream, timeUnit);
    }

    /**
     * Returns a map of counters by type from a given stopwatch.
     *
     * @param stopwatch the stopwatch which counters are to be grouped
     */
    protected static Map<Type, List<Counter>> groupCountersByType(Stopwatch stopwatch)
    {
        return groupCountersByType(stopwatch.getCounters());
    }

    /**
     * Returns a map of counters by type from a given counters list
     *
     * @param counters a list containing counters to be grouped
     */
    protected static Map<Type, List<Counter>> groupCountersByType(List<Counter> counters)
    {
        Map<Type, List<Counter>> countersByType = new EnumMap<>(Type.class);
        for (Counter counter : counters)
        {
            countersByType.compute(counter.getType(), (Type type, List<Counter> internalList) ->
            {
                if (internalList == null)
                {
                    internalList = new ArrayList<>();
                }
                internalList.add(counter);
                return internalList;
            });
        }
        return countersByType;
    }

    /**
     * Prints the statistics for the given counters in the specified print stream.
     *
     * @param counters    the counters to be printed
     * @param printStream the print stream to which statistics will be sent
     * @throws NullPointerException if a null collection or print stream is received
     */
    public static void print(List<Counter> counters, PrintStream printStream)
    {
        printStream.print(toTableFormat(groupCountersByType(counters), null));
    }

    /**
     * Prints the statistics for the given counters in the specified print stream.
     *
     * @param counters    the counters to be printed
     * @param printStream the print stream to which statistics will be sent
     * @param timeUnit    the time unit in which elapsed times will be displayed; if null, the
     *                    default time unit specified for each counter will be applied
     * @throws NullPointerException if a null collection or print stream is received
     */
    public static void print(List<Counter> counters, PrintStream printStream, TimeUnit timeUnit)
    {
        printStream.print(toTableFormat(groupCountersByType(counters), timeUnit));
    }

    /**
     * Prints the statistics for the given counters in the specified print stream.
     *
     * @param countersByType the counters to be printed
     * @param printStream    the print stream to which statistics will be sent
     * @throws NullPointerException if a null collection or print stream is received
     */
    public static void print(Map<Type, List<Counter>> countersByType, PrintStream printStream)
    {
        printStream.print(toTableFormat(countersByType, null));
    }

    /**
     * Prints the statistics for the given counters in the specified print stream.
     *
     * @param countersByType the counters to be printed
     * @param printStream    the print stream to which statistics will be sent
     * @param timeUnit       the time unit in which elapsed times will be displayed; if null,
     *                       the default time unit specified for each counter will be applied
     * @throws NullPointerException if a null collection or print stream is received
     */
    public static void print(Map<Type, List<Counter>> countersByType, PrintStream printStream,
            TimeUnit timeUnit)
    {
        printStream.print(toTableFormat(countersByType, timeUnit));
    }

    /**
     * Returns a table with counters and elapsed times.
     *
     * @param counters the counters whose data will be fetched
     * @return as formatted string containing a table rows with all counters and elapsed times
     */
    protected static String toTableFormat(Map<Type, List<Counter>> counters)
    {
        return toTableFormat(counters, null);
    }

    /**
     * Returns a table with counters and elapsed times in a given time unit.
     *
     * @param counters the counters whose data will be fetched
     * @param timeUnit the time unit in which elapsed times will be displayed; if null, the
     *                 default time unit specified for each counter will be applied
     * @return a formatted string containing a table rows with all counters and elapsed times
     */
    protected static String toTableFormat(Map<Type, List<Counter>> counters, TimeUnit timeUnit)
    {
        StringBuilder builder = new StringBuilder();
        builder.append(COUNTERS_TABLE_ROW_SEPARATOR);
        builder.append(COUNTERS_TABLE_HEADER);
        builder.append(COUNTERS_TABLE_ROW_SEPARATOR);

        for (Entry<Type, List<Counter>> entries : counters.entrySet())
        {
            builder.append(timeUnit == null ? toTotalRowFormat(entries.getValue())
                    : toTotalRowFormat(entries.getValue(), timeUnit));
        }
        builder.append(COUNTERS_TABLE_ROW_SEPARATOR);
        builder.append(LINE_SEPARATOR);

        return builder.toString();
    }

    /**
     * Returns a row with the given counter. The elapsed time will be printed using the
     * default time unit.
     *
     * @param counter the counter whose data will be fetched
     * @return a formatted String representing a row with the data fetched from the counter
     */
    protected static String toRowFormat(Counter counter)
    {
        return toRowFormat(counter, counter.getTimeUnit());
    }

    /**
     * Returns a row with the given counter. The elapsed time will be printed using the given
     * time unit.
     *
     * @param counter  the counter whose data will be fetched
     * @param timeUnit the time unit in which the counter's elapsed time will be displayed
     *                 (cannot be null)
     * @return a formatted String representing a row with the data fetched from the counter
     */
    protected static String toRowFormat(Counter counter, TimeUnit timeUnit)
    {
        return String.format(COUNTERS_TABLE_ROW_FORMAT, counter.getType(), formatElapsedTime(counter, timeUnit),
                timeUnit.toString().toLowerCase());
    }

    /**
     * Returns a row with the given counter. The elapsed time will be printed.
     *
     * @param counters the counter which total is to be calculated
     * @return a formatted String representing the sum of all elapsed times
     */
    protected static String toTotalRowFormat(List<Counter> counters)
    {
        if (counters.isEmpty())
        {
            throw new IllegalArgumentException("At least one counter is required");
        }
        return toTotalRowFormat(counters, counters.get(0).getTimeUnit());
    }

    /**
     * Returns a row with the given counter. The elapsed time will be printed.
     *
     * @param counters the counter which total is to be calculated
     * @return a formatted String representing the sum of all elapsed times
     */
    protected static String toTotalRowFormat(List<Counter> counters, TimeUnit timeUnit)
    {
        if (counters.isEmpty())
        {
            throw new IllegalArgumentException("At least one counter is required");
        }

        String type = counters.get(0).getType().toString();

        double elapsedTime = counters.stream().map(counter -> counter.elapsedTime(timeUnit)).reduce(0.0, Double::sum);

        return String.format(COUNTERS_TABLE_ROW_FORMAT, type, formatElapsedTime(elapsedTime),
                timeUnit.toString().toLowerCase());
    }

    /**
     * Returns the elapsed time, formatted.
     *
     * @param elapsedTime the time to be formatted
     * @return a formatted String for the elapsed time
     */
    protected static String formatElapsedTime(double elapsedTime)
    {
        DecimalFormat decimalFormat = new DecimalFormat(ELAPSED_TIME_FORMAT);
        decimalFormat.setMaximumFractionDigits(ConfigurationHolder.getConfiguration().getScale());
        return decimalFormat.format(elapsedTime);
    }

    /**
     * Returns the elapsed time of a counter in a given time unit, formatted.
     *
     * @param counter  the counter whose data will be fetched
     * @param timeUnit the time unit in which the counter's elapsed time will be displayed
     *                 (cannot be null)
     * @return a formatted String for the elapsed time
     */
    protected static String formatElapsedTime(Counter counter, TimeUnit timeUnit)
    {
        return formatElapsedTime(counter.elapsedTime(timeUnit));
    }

}
