/*
 * Copyright 2021 obvj.net
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.obvj.performetrics.util.print;

import java.io.PrintStream;

import net.obvj.performetrics.Stopwatch;
import net.obvj.performetrics.config.ConfigurationHolder;

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
     * <p>
     * The default {@link PrintStyle} will be applied.
     *
     * @param stopwatch   the stopwatch to be printed
     * @param printStream the print stream to which data will be sent
     *
     * @throws NullPointerException if a null stopwatch or print stream is received
     *
     * @since 2.2.1
     */
    public static void printSummary(Stopwatch stopwatch, PrintStream printStream)
    {
        printSummary(stopwatch, printStream, null);
    }

    /**
     * Prints summarized elapsed times from the given stopwatch in the specified print stream,
     * with a custom {@link PrintStyle}.
     *
     * @param stopwatch   the stopwatch to be printed
     * @param printStream the print stream to which data will be sent
     * @param printStyle  the {@link PrintStyle} to be applied; if {@code null}, the default
     *                    PrintStyle will be applied
     *
     * @throws NullPointerException     if a null stopwatch or print stream is received
     * @throws IllegalArgumentException if the specified PrintStyle is not compatible with
     *                                  {@link PrintFormat#SUMMARIZED}
     *
     * @since 2.2.1
     */
    public static void printSummary(Stopwatch stopwatch, PrintStream printStream, PrintStyle printStyle)
    {
        PrintStyle style = printStyle != null ? printStyle
                : ConfigurationHolder.getConfiguration().getPrintStyleForSummary();
        printStream.print(PrintFormat.SUMMARIZED.format(stopwatch, style));
    }

    /**
     * Prints detailed information about timing sessions from the given stopwatch in the
     * specified print stream.
     * <p>
     * The default {@link PrintStyle} will be applied.
     *
     * @param stopwatch   the stopwatch to be printed
     * @param printStream the print stream to which information will be sent
     *
     * @throws NullPointerException if a null stopwatch or print stream is received
     *
     * @since 2.2.1
     */
    public static void printDetails(Stopwatch stopwatch, PrintStream printStream)
    {
        printDetails(stopwatch, printStream, null);
    }

    /**
     * Prints detailed information about timing sessions from the given stopwatch in the
     * specified print stream, with a custom {@link PrintStyle}.
     *
     * @param stopwatch   the stopwatch to be printed
     * @param printStream the print stream to which information will be sent
     * @param printStyle  the {@link PrintStyle} to be applied; if {@code null}, the default
     *                    PrintStyle will be applied
     *
     * @throws NullPointerException     if a null stopwatch or print stream is received
     * @throws IllegalArgumentException if the specified PrintStyle is not compatible with
     *                                  {@link PrintFormat#DETAILED}
     *
     * @since 2.2.1
     */
    public static void printDetails(Stopwatch stopwatch, PrintStream printStream, PrintStyle printStyle)
    {
        PrintStyle style = printStyle != null ? printStyle
                : ConfigurationHolder.getConfiguration().getPrintStyleForDetails();
        printStream.print(PrintFormat.DETAILED.format(stopwatch, style));
    }

}
