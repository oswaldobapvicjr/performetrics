package net.obvj.performetrics.util.printer;

import java.io.PrintStream;

import net.obvj.performetrics.Stopwatch;

/**
 * This class groups all custom printing operations in a single place.
 *
 * @author oswaldo.bapvic.jr
 */
public class PrintUtils
{

    /**
     * This is a utility class, not meant to be instantiated.
     */
    private PrintUtils()
    {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Prints summarized elapsed times from the given stopwatch in the specified print stream.
     *
     * @param stopwatch   the stopwatch to be printed
     * @param printStream the print stream to which data will be sent
     * @throws NullPointerException if a null stopwatch or print stream is received
     */
    public static void printSummary(Stopwatch stopwatch, PrintStream printStream)
    {
        printStream.print(StopwatchFormatter.SUMMARIZED.format(stopwatch));
    }

}
