package net.obvj.performetrics.util.printer;

import java.io.PrintStream;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import net.obvj.performetrics.Counter;
import net.obvj.performetrics.Stopwatch;

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
    public static void printStopwatch(Stopwatch stopwatch, PrintStream printStream)
    {
        printStopwatch(stopwatch, printStream, null);
    }

    /**
     * Prints the statistics for the given counters in the specified print stream.
     *
     * @param counters    the counters to be printed
     * @param printStream the print stream to which statistics will be sent
     * @throws NullPointerException if a null stopwatch or print stream is received
     */
    public static void printCounters(Collection<Counter> counters, PrintStream printStream)
    {
        printCounters(counters, printStream, null);
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
    public static void printStopwatch(Stopwatch stopwatch, PrintStream printStream, TimeUnit timeUnit)
    {
        printCounters(stopwatch.getAllCounters(), printStream, timeUnit);
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
    public static void printCounters(Collection<Counter> counters, PrintStream printStream, TimeUnit timeUnit)
    {
        printStream.print(toTableFormat(counters, timeUnit));
    }

    /**
     * Returns a table with counters and elapsed times
     *
     * @param counters the counters whose data will be fetched
     * @return as formatted string containing a table rows with all counters and elapsed times
     */
    protected static String toTableFormat(Collection<Counter> counters)
    {
        return toTableFormat(counters, null);
    }

    /**
     * Returns a table with counters and elapsed times in a given time unit.
     *
     * @param counters the counters whose data will be fetched
     * @param timeUnit the time unit in which elapsed times will be displayed; if null, the
     *                 default time unit specified for each counter will be applied
     * @return as formatted string containing a table rows with all counters and elapsed times
     */
    protected static String toTableFormat(Collection<Counter> counters, TimeUnit timeUnit)
    {
        StringBuilder builder = new StringBuilder();
        builder.append(COUNTERS_TABLE_ROW_SEPARATOR);
        builder.append(COUNTERS_TABLE_HEADER);
        builder.append(COUNTERS_TABLE_ROW_SEPARATOR);

        for (Counter counter : counters)
        {
            builder.append(timeUnit == null ? toRowFormat(counter) : toRowFormat(counter, timeUnit));
        }
        builder.append(COUNTERS_TABLE_ROW_SEPARATOR);
        builder.append(LINE_SEPARATOR);

        return builder.toString();
    }

    /**
     * Returns a row with the given counter. The elapsed time will be printed using the
     * counter's default time unit.
     *
     * @param counter the counter whose data will be fetched
     * @return a formatted String representing a row with the data fetched from the counter
     */
    protected static String toRowFormat(Counter counter)
    {
        return toRowFormat(counter, counter.getDefaultTimeUnit());
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
     * Returns the elapsed time of a counter in a given time unit, formatted.
     *
     * @param counter  the counter whose data will be fetched
     * @param timeUnit the time unit in which the counter's elapsed time will be displayed
     *                 (cannot be null)
     * @return a formatted String for the elapsed time
     */
    protected static String formatElapsedTime(Counter counter, TimeUnit timeUnit)
    {
        return new DecimalFormat(ELAPSED_TIME_FORMAT).format(counter.elapsedTime(timeUnit));
    }

}
