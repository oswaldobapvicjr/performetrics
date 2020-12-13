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
     * Prints the statistics for the given stopwatch in the specified print stream.
     *
     * @param stopwatch   the stopwatch to be printed
     * @param printStream the print stream to which statistics will be sent
     * @throws NullPointerException if a null stopwatch or print stream is received
     */
    public static void print(Stopwatch stopwatch, PrintStream printStream)
    {
        printStream.print(StopwatchFormatter.DETAILED.format(stopwatch, PrintStyle.DETAILED_HORIZONTAL_LINES));
    }

}
