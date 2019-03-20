package net.obvj.performetrics.util.printer;

import java.io.PrintStream;

import net.obvj.performetrics.Counter;
import net.obvj.performetrics.Stopwatch;

/**
 * This class groups all custom printing operations in a single place.
 * 
 * @author oswaldo.bapvic.jr
 */
public class PrintUtils
{
    /**
     * Prints the statistics for the given counter in the specified print stream.
     *
     * @param printStream the print stream to which statistics will be sent
     * @throws NullPointerException if a null stopwatch or print stream is received
     */
    public static void printStopwatch(Stopwatch stopwatch, PrintStream printStream)
    {
        String rowFormat = "\n| %-15s | %20s | %-12s |";
        StringBuilder builder = new StringBuilder();
        String header = String.format(rowFormat, "Counter", "Elapsed time", "Time unit");
        String separator = String.format(rowFormat, "", "", "").replace(" ", "-").replace("|", "+");
        builder.append(separator);
        builder.append(header);
        builder.append(separator);
        for (Counter counter : stopwatch.getAllCounters())
        {
            builder.append(String.format(rowFormat, counter.getType(), counter.elapsedTime(), counter.getTimeUnit()));
        }
        builder.append(separator);
        builder.append("\n");
        printStream.print(builder.toString());
    }

}
