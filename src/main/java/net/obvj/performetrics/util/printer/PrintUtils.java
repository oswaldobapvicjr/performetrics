package net.obvj.performetrics.util.printer;

import java.io.PrintStream;
import java.util.Collection;

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
    protected static final String COUNTERS_TABLE_ROW_FORMAT = LINE_SEPARATOR + "| %-15s | %20s | %-12s |";
    protected static final String COUNTERS_TABLE_ROW_SEPARATOR = String.format(COUNTERS_TABLE_ROW_FORMAT, "", "", "")
            .replace(" ", "-").replace("|", "+");
    protected static final String COUNTERS_TABLE_HEADER = String.format(COUNTERS_TABLE_ROW_FORMAT, "Counter",
            "Elapsed time", "Time unit");

    private PrintUtils()
    {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Prints the statistics for the given counter in the specified print stream.
     *
     * @param printStream the print stream to which statistics will be sent
     * @throws NullPointerException if a null stopwatch or print stream is received
     */
    public static void printStopwatch(Stopwatch stopwatch, PrintStream printStream)
    {
        StringBuilder builder = new StringBuilder();
        builder.append(COUNTERS_TABLE_ROW_SEPARATOR);
        builder.append(COUNTERS_TABLE_HEADER);
        builder.append(COUNTERS_TABLE_ROW_SEPARATOR);
        builder.append(toRowFormat(stopwatch.getAllCounters()));
        builder.append(COUNTERS_TABLE_ROW_SEPARATOR);
        builder.append(LINE_SEPARATOR);
        printStream.print(builder.toString());
    }

    /**
     * Returns a row for each counter
     *
     * @param counters the counters whose data will be fetched
     * @return as formatted string containing rows for each counter
     */
    protected static String toRowFormat(Collection<Counter> counters)
    {
        StringBuilder builder = new StringBuilder();
        for (Counter counter : counters)
        {
            builder.append(toRowFormat(counter));
        }
        return builder.toString();
    }

    /**
     * Returns a row with the given counter
     *
     * @param counter the counter whose data will be fetched
     * @return a formatted String representing a row with the data fetched from the counter
     */
    protected static String toRowFormat(Counter counter)
    {
        return String.format(COUNTERS_TABLE_ROW_FORMAT, counter.getType(), counter.elapsedTime(),
                counter.getTimeUnit());
    }

}
